/* An actor that iterates a contained actor over input arrays.

 Copyright (c) 2004-2006 The Regents of the University of California.
 All rights reserved.
 Permission is hereby granted, without written agreement and without
 license or royalty fees, to use, copy, modify, and distribute this
 software and its documentation for any purpose, provided that the above
 copyright notice and the following two paragraphs appear in all copies
 of this software.

 IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
 FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
 ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
 PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 ENHANCEMENTS, OR MODIFICATIONS.

 PT_COPYRIGHT_VERSION_2
 COPYRIGHTENDKEY

 */
package ptolemy.actor.lib.hoc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import ptolemy.actor.Actor;
import ptolemy.actor.Director;
import ptolemy.actor.Executable;
import ptolemy.actor.IOPort;
import ptolemy.actor.NoTokenException;
import ptolemy.actor.QueueReceiver;
import ptolemy.actor.Receiver;
import ptolemy.actor.util.Time;
import ptolemy.data.DoubleToken;
import ptolemy.data.Token;
import ptolemy.data.expr.Parameter;
import ptolemy.data.type.BaseType;
import ptolemy.kernel.ComponentEntity;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.Attribute;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.InternalErrorException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.kernel.util.Settable;
import ptolemy.util.MessageHandler;

//////////////////////////////////////////////////////////////////////////
//// RealTimeComposite

/**
 This is a container for another actor that fires that other actor
 at real times corresponding to the input time stamps. Its
 ports are those of the contained actor. Given one or more events
 with time stamp <i>t</i> at the input ports, it queues the events
 to provide to a firing of the contained actor that is deferred to
 occur when real time (since start of execution, in seconds) exceeds
 or matches <i>t</i>.  If real time already exceeds <i>t</i>, then the firing
 may occur immediately.
 <p>
 In addition to the parameters of the contained actor, this actor
 has a <i>delay</i> parameter. The value of this parameter is
 the minimum delay (in model time) between an input event and
 an output event that results from that input event.
 If the enclosed actor produces no output, or if the time
 of the outputs can be arbitrarily whatever current time
 is in the model when they are produced, then <i>delay</i>
 should be set to <i>undefined</i>. This is the default value.
 With this value, the enclosed actor is
 executed in a separate thread.
 If the firing produces output events, then those are given time
 stamps equal to the greater of the current model time of the
 enclosing model and the current real time at which the outputs
 are produced (in seconds since the start of execution). In
 this case, the enclosed actor
 does not regulate in any way the passage of time of the
 enclosing model, so the time stamps of the enclosing model
 could get arbitrarily far ahead of real time.
 <p>
 If the value of <i>delay</i> is 0.0 (zero), then the inside
 model is run in the same thread as the enclosing model.
 When this RealTimeComposite fires, the fire() method stalls
 until real time matches the current time of the model, and
 then invokes the enclosed model. If the enclosed model produces
 any outputs, then those outputs have time stamps equal to the
 time stamps of the input. Hence, from the perspective of DE
 semantics, this actor has zero delay, even though it can
 introduce real-time delay (which is indistinguishable from
 just taking a long time to evaluate the fire() method).
 Note that with <i>delay</i> = 0.0, this actor affects the
 model in way similar to the <i>synchronizeToRealTime</i>
 parameter of the director, except that only the events
 provided to this actor are synchronized to real time, rather
 than all events.
 <p>
 If the value of <i>delay</i> is positive, then the inside
 model is run in a separate thread, just as if the value
 were undefined, but in this case, this actor does
 regulate the passage of time of the enclosing model.
 In particular, given an event with time stamp <i>t</i>
 it prevents model time from advancing past <i>t</i>
 + <i>delay</i> until the firing triggered by the event
 has completed. Any outputs produced by that firing are
 assigned time stamps equal to the greater of <i>t</i>
 + <i>delay</i> and the current real time at which the
 output is produced.
 
 FIXME: Do we want instead a fixed output time stamp?
 Then the semantics is the same as when <i>delay</i> = 0.
 But then can the actor be used to measure the time
 of execution?
 
 when the fire() method of this
 RealTimeComposite actor is invoked, the calling thread
 is stalled until real time matches or exceeds model
 time minus the specified delay.  If the enclosing model
 produces outputs, they will be assigned the greater of
 current model time and current real time, just as with
 undefined. Note, however, that since the enclosing
 model gets stalled, output time stamps FIXME
 
 FIXME: Document delay and fix what follows.

 The firing always occurs in another thread.
 <p>
 For various reasons, this actor is tricky to use. The most natural
 domain to use it in is DE, providing it with input events with time
 stamps that specify when to perform some action, such as an actuator
 or display action. However, if the DE system is an open-loop system,
 then model time of the DE system can get very far ahead of the
 RealTimeComposite. It is helpful to use a feedback loop including
 this RealTimeComposite to keep the DE model from getting ahead.
 <p>
 This actor may also be used in SDF and SR, but those cases, it
 will likely be necessary to set <i>synchronizeToRealTime</i>.
 This actor consumes its inputs and schedules execution in
 its postfire() method, and hence in SR will behave as a strict
 actor (all inputs must be known for anything to happen).
 <p>
 FIXME On a finite run, the associated threads just hangs on a take().
 How to stop it?
 <p>
 FIXME: If there is a PortParameter, the parameter gets updated when the
 fire() method of this composite is invoked, which creates a nondeterminate
 interaction with the deferred execution. See CompositeActor.fire().

 @author Edward A. Lee
 @version $Id$
 @since Ptolemy II 4.1
 @Pt.ProposedRating Yellow (eal)
 @Pt.AcceptedRating Red (neuendor)
 */
public class RealTimeComposite extends MirrorComposite {
    /** Create an actor with a name and a container.
     *  The container argument must not be null, or a
     *  NullPointerException will be thrown.  This actor will use the
     *  workspace of the container for synchronization and version counts.
     *  If the name argument is null, then the name is set to the empty string.
     *  Increment the version of the workspace.
     *  @param container The container actor.
     *  @param name The name of this actor.
     *  @exception IllegalActionException If the container is incompatible
     *   with this actor.
     *  @exception NameDuplicationException If the name coincides with
     *   an actor already in the container.
     */
    public RealTimeComposite(CompositeEntity container, String name)
            throws IllegalActionException, NameDuplicationException {
        super(container, name);
        setClassName("ptolemy.actor.lib.hoc.RealTimeComposite");
        new RealTimeDirector(this, "RealTimeDirector");
        
        // Hidden parameter defining "undefined".
        Parameter undefined = new Parameter(this, "undefined");
        undefined.setVisibility(Settable.EXPERT);
        undefined.setPersistent(false);
        undefined.setExpression("-1.0");
        
        delay = new Parameter(this, "delay");
        delay.setTypeEquals(BaseType.DOUBLE);
        delay.setExpression("undefined");
    }

    ///////////////////////////////////////////////////////////////////
    ////                         parameters                        ////

    /** The maximum model-time delay between the input events and the
     *  output events. This is a double that defaults to <i>undefined</i>.
     */
    public Parameter delay;
    
    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /** React to a change in an attribute.  This method is called by
     *  a contained attribute when its value changes.  In this base class,
     *  the method does nothing.  In derived classes, this method may
     *  throw an exception, indicating that the new attribute value
     *  is invalid.  It is up to the caller to restore the attribute
     *  to a valid value if an exception is thrown.
     *  @param attribute The attribute that changed.
     *  @exception IllegalActionException If the change is not acceptable
     *   to this container (not thrown in this base class).
     */
    public void attributeChanged(Attribute attribute)
            throws IllegalActionException {
        if (attribute == delay) {
            _delayValue = ((DoubleToken)delay.getToken()).doubleValue();
        } else {
            super.attributeChanged(attribute);
        }
    }

    /** Invoke iterations on the contained actor of the
     *  container of this director repeatedly until either it runs out
     *  of input data or prefire() returns false. If postfire() of any
     *  actor returns false, then return false. Otherwise, return true.
     *  @return True to allow the thread to continue executing.
     *  @exception IllegalActionException If any called method of
     *   of the contained actor throws it, or if the contained
     *   actor is not opaque.
     */
    public boolean fireContainedActors() throws IllegalActionException {
        // Don't call "super.fire();" here, this actor contains its
        // own director.
        Iterator actors = entityList().iterator();
        boolean postfireReturns = true;

        while (actors.hasNext() && !_stopRequested) {
            Actor actor = (Actor) actors.next();

            if (!((ComponentEntity) actor).isOpaque()) {
                throw new IllegalActionException(this,
                        "Inside actor is not opaque "
                                + "(perhaps it needs a director).");
            }

            int result = Executable.COMPLETED;

            while (result != Executable.NOT_READY) {
                if (_debugging) {
                    _debug("Iterating actor: " + actor.getFullName());
                }
                if (_debugging) {
                    _debug("---- Iterating actor in associated thread: " + actor.getFullName());
                }
                result = actor.iterate(1);

                // Should return if there are no more input data,
                // irrespective of return value of prefire() of
                // the actor, which is not reliable.
                boolean outOfData = true;
                Iterator inPorts = actor.inputPortList().iterator();

                while (inPorts.hasNext()) {
                    IOPort port = (IOPort) inPorts.next();

                    for (int i = 0; i < port.getWidth(); i++) {
                        if (port.hasToken(i)) {
                            outOfData = false;
                            break;
                        }
                    }
                }

                if (outOfData) {
                    break;
                }

                if (result == Executable.STOP_ITERATING) {
                    if (_debugging) {
                        _debug("---- Actor requests halt: "
                                + actor.getFullName());
                    }
                    postfireReturns = false;
                    break;
                }
            }
        }
        return postfireReturns;
    }
    
    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    /** The cached value of the <i>delay</i> parameter. */
    private double _delayValue = 0.0;
    
    /** Queue of unprocessed input events. */
    private DelayQueue<InputFrame> _inputFrames = new DelayQueue<InputFrame>();
    
    /** Queue of unprocessed output events. */
    private Queue<OutputFrame> _outputFrames = new LinkedList<OutputFrame>();

    /** The real time at which the model begins executing, in milliseconds. */
    private long _realStartTime = 0;
    
    ///////////////////////////////////////////////////////////////////
    ////                         inner classes                     ////

    ///////////////////////////////////////////////////////////////////
    //// InputFrame

    /** Bundle of a token and the input port at which it arrived.
     *  Use null for <i>theTokens</i> specifies this frame as a "stop frame" to
     *  flag that no more inputs will be delivered.
     *  @param theTime The model time of the input events.
     *  @param theTokens The tokens in the input events.
     */
    private class InputFrame implements Delayed {
        public InputFrame(Time theTime, List<QueuedToken> theTokens) {
            tokens = theTokens;
            time = theTime;
        }
        public final Time time;
        public final List<QueuedToken> tokens;
        public long getDelay(TimeUnit unit) {
            // Calculate time to wait.
            long elapsedTime = System.currentTimeMillis() - _realStartTime;
            // NOTE: We assume that the elapsed time can be
            // safely cast to a double.  This means that
            // the DE domain has an upper limit on running
            // time of Double.MAX_VALUE milliseconds.
            double elapsedTimeInSeconds = elapsedTime / 1000.0;
            long timeToWait = (long) (time.subtract(elapsedTimeInSeconds).getDoubleValue() * 1000.0);
            return unit.convert(timeToWait, TimeUnit.MILLISECONDS);
        }
        public int compareTo(Delayed frame) {
            // NOTE: We assume that only comparisons against instances of Frame will be done.
            // Is this safe?
            return time.compareTo(((InputFrame)frame).time);
        }
    }

    ///////////////////////////////////////////////////////////////////
    //// QueuedToken

    /** Bundle of a token and the input port and channel
     *  at which it arrived.
     */
    private class QueuedToken {
        public QueuedToken(IOPort thePort, int theChannel, Token theToken) {
            token = theToken;
            channel = theChannel;
            port = thePort;
        }
        public final int channel;
        public final Token token;
        public final IOPort port;
        public String toString() {
            return "token " + token + " for port " + port.getFullName() + "(" + channel + ")";
        }
    }

    ///////////////////////////////////////////////////////////////////
    //// OutputFrame

    /** Bundle of a token and the output port at which it arrived.
     *  @param theTime The model time of the output events.
     *  @param theTokens The tokens in the output events.
     */
    private class OutputFrame {
        public OutputFrame(Time theTime, List<QueuedToken> theTokens) {
            tokens = theTokens;
            time = theTime;
        }
        public final Time time;
        public final List<QueuedToken> tokens;
    }

    ///////////////////////////////////////////////////////////////////
    //// RealTimeDirector

    /** This is a specialized director that defers firing of the
     *  contained actors until real-time matches the time stamp of
     *  provided inputs. It does this in a separate thread that
     *  blocks until the times match, then transfers the input tokens
     *  that arrived with that time stamp and fires the contained actors
     *  in the order in which they appear in the actor list repeatedly
     *  until either there is no more input data for the actor or
     *  the prefire() method of the actor returns false. If postfire()
     *  of any actor returns false, then postfire() of this director
     *  will return false, requesting a halt to execution of the model.
     */
    private class RealTimeDirector extends Director {
        /** Create a new instance of the director for RealTimeComposite.
         *  @param container The container for the director.
         *  @param name The name of the director.
         *  @exception IllegalActionException Not thrown in this base class.
         *  @exception NameDuplicationException Not thrown in this base class.
         */
        public RealTimeDirector(CompositeEntity container, String name)
                throws IllegalActionException, NameDuplicationException {
            super(container, name);
            setPersistent(false);
        }

        /** If current model time matches the time at which outputs
         *  that have been queued should be produced, then produce them.
         *  Yield to other threads. 
         *  @exception IllegalActionException If production of an output
         *   fails (e.g. type error).
         */
        public void fire() throws IllegalActionException {
            if (_realStartTime < 0L) {
                _realStartTime = System.currentTimeMillis();
            }
            if (_delayValue == 0.0) {
                // Delay is zero, so wait until current time matches
                // model time, and then treat this as an ordinary composite actor.
                long realTimeMillis = System.currentTimeMillis() - _realStartTime;
                long modelTimeMillis = Math.round(getDirector().getModelTime().getDoubleValue() * 1000.0);
                if (realTimeMillis < modelTimeMillis) {
                    try {
                        Thread.sleep(modelTimeMillis - realTimeMillis);
                    } catch (InterruptedException e) {
                        // Ignore and continue.
                    }
                }
                // FIXME: This isn't quite right, since this will postfire()
                // contained actors.
                super.fire();
            } else {
                // Delay is either undefined or positive,
                // so we are running in separate thread.
                OutputFrame frame = _outputFrames.peek();
                if (frame != null && frame.time.equals(getModelTime())) {
                    // Produce the outputs on the frame.
                    for (QueuedToken token : frame.tokens) {
                        if (token.channel < token.port.getWidth()) {
                            token.port.send(token.channel, token.token);
                        }
                    }
                }
                Thread.yield();
                if (_delayValue > 0) {
                    // Delay value is positive. Stall the environment
                    // until real time exceeds model time minus the delay.
                    // FIXME: Do that!
                }
            }
        }

        /** Delegate by calling fireAt() on the director of the container's
         *  container.
         *  @param actor The actor requesting firing.
         *  @param time The time at which to fire.
         */
        public void fireAt(Actor actor, Time time)
                throws IllegalActionException {
            Director director = RealTimeComposite.this.getExecutiveDirector();
            if (director != null) {
                if (RealTimeComposite.this._debugging) {
                    RealTimeComposite.this._debug("---- Actor requests firing at time "
                            + time
                            + ": "
                            + actor.getFullName());
                }
                director.fireAt(RealTimeComposite.this, time);
            }
        }

        /** Fire the specified actor at the first opportunity
         *  and then pass the request up to the executive director.
         *  When passing it up, request a firing at the greater of
         *  the current time of that director or the elapsed real
         *  time since the start of the model.
         *  This is useful for actors that spontaneously produce output,
         *  e.g. from sensor data or from completion of some previously
         *  started task. The firing of the actor will produce the output,
         *  sending it to the inside of the output ports of this composite,
         *  and then the firing of the composite will transfer those tokens
         *  to the outside model.
         *  @param actor The actor requesting firing (ignored).
         *  @param time The time at which to fire.
         */
        public void fireAtCurrentTime(Actor actor)
                throws IllegalActionException {
            _inputFrames.put(new InputFrame(getModelTime(), new LinkedList<QueuedToken>()));
            Director director = RealTimeComposite.this.getExecutiveDirector();
            if (director != null) {
                // We assume that the contained actors mean "real time" by
                // "current time". Hopefully, this will be in the future w.r.t. model time.
                // Use fireAt() hoping that the director will not increment time
                // too soon.
                // FIXME: This is not right!
                Time time = new Time(this, (System.currentTimeMillis() - _realStartTime) / 1000.0);
                if (RealTimeComposite.this._debugging) {
                    RealTimeComposite.this._debug(
                            "----- fireAtCurrentTime() request by actor "
                            + actor.getFullName()
                            + ". Model time is "
                            + getModelTime()
                            + ", and real time is "
                            + time);
                }
                director.fireAt(RealTimeComposite.this, time);
            }
        }
        
        /** Return the current time of the enclosing actor.
         *  @return The current time.
         */
        public Time getModelTime() {
            return ((Actor)getContainer()).getExecutiveDirector().getModelTime();
        }

        /** Start the associated thread.
         *  @exception IllegalActionException If the initialize() method of
         *   one of the associated actors throws it.
         */
        public void initialize() throws IllegalActionException {
            // The superclass will initialize all the actors.
            super.initialize();
            // Set a flag indicating that the first firing should
            // initialize the _realStartTime variable. This is done
            // in the first firing to be as late as possible, so
            // that startup transients are minimized.
            // FIXME: This will impede synchronization with other
            // actors, since there won't be a common time base.
            _realStartTime = -1L;
            if (_delayValue != 0) {
                // We will be executing in a new thread.
                // Create and start that thread.
                _inputFrames.clear();
                _outputFrames.clear();
                _thread = new RealTimeThread();
                _thread.start();
            }
        }

        /** Return a new instance of QueueReceiver.
         *  @return A new instance of QueueReceiver.
         *  @see QueueReceiver
         */
        public Receiver newReceiver() {
            return new QueueReceiver();
        }

        /** Clear the list of input events for this iteration and return true
         *  if the associated thread is alive, if <i>delay</i> is not 0.0.
         *  Otherwise, return true.
         *  @return True if the associated thread is still alive, or true
         *   if delay == 0.0.
         */
        public boolean prefire() throws IllegalActionException {
            // Superclass aligns current time to that of the container.
            boolean returnValue = super.prefire();
            
            if (_delayValue != 0) {
                // Have to create a new list because the previous list may
                // not have been consumed yet.
                _inputTokens = new LinkedList<QueuedToken>();
                return _thread.isAlive();
            } else {
                return returnValue;
            }
        }

        /** Send all the collected tokens to the queue for consumption
         *  by the associated thread, if there is an associated thread.
         *  Otherwise, just invoke the superclass postfire().
         *  @return True if the associated thread is still alive.
         */
        public boolean postfire() throws IllegalActionException {
            boolean result = super.postfire();
            if (_delayValue != 0) {
                if (_inputTokens.size() > 0) {
                    if (RealTimeComposite.this._debugging) {
                        RealTimeComposite.this._debug(
                                "Queueing input tokens for the associated thread: "
                                + _inputTokens.toString()
                                + " to be processed at time "
                                + getModelTime());
                    }
                    _inputFrames.put(new InputFrame(getModelTime(), _inputTokens));
                }
                OutputFrame frame = _outputFrames.peek();
                if (frame != null && frame.time.equals(getModelTime())) {
                    // Consume the outputs on the frame, which will have
                    // been sent in the fire() method.
                    _outputFrames.poll();
                }
                result = _thread.isAlive();
            }
            return result;
        }
        
        /** Override the base class to post a "stop frame" on the queue
         *  if there is an associated thread.
         */
        public void stop() {
            if (_delayValue != 0) {
                if (RealTimeComposite.this._debugging) {
                    RealTimeComposite.this._debug(
                            "Queueing a stop-frame token for the associated thread with time: "
                            + getModelTime());
                }
                _inputFrames.put(new InputFrame(getModelTime(), null));
            } else {
                super.stop();
            }
        }

        /** Record data from the specified input port
         *  for transfer to the queue used to communicate these data to the
         *  associated thread.
         *  @exception IllegalActionException If reading the inputs fails.
         *  @param port The port to transfer tokens from.
         *  @return True if at least one data token is transferred.
         */
        public boolean transferInputs(IOPort port)
                throws IllegalActionException {
            if (_delayValue == 0) {
                return super.transferInputs(port);
            }
            boolean result = false;

            for (int i = 0; i < port.getWidth(); i++) {
                try {
                    if (port.isKnown(i)) {
                        if (port.hasToken(i)) {
                            Token token = port.get(i);
                            _inputTokens.add(new QueuedToken(port, i, token));
                            if (RealTimeComposite.this._debugging) {
                                RealTimeComposite.this._debug(
                                        getName(), "transferring input from "
                                        + port.getName());
                            }
                            result = true;
                        }
                    }
                } catch (NoTokenException ex) {
                    // this shouldn't happen.
                    throw new InternalErrorException(this, ex, null);
                }
            }
            return result;
        }

        /** If real time is less than or equal to the current model time
         *  of the environment, then produce the outputs immediately at the
         *  current model time. Otherwise, collect them and queue them to
         *  be produced by the fire method when model time matches the
         *  current real time, and call fireAt() to request a firing
         *  at that time.
         *  @exception IllegalActionException If reading the inputs fails.
         *  @param port The port to transfer tokens from.
         *  @return True if at least one data token is produced now.
         */
        public boolean transferOutputs(IOPort port)
                throws IllegalActionException {
            if (_delayValue == 0) {
                return super.transferOutputs(port);
            }
            Time time = getModelTime();
            double realTimeInSeconds = (System.currentTimeMillis() - _realStartTime) / 1000.0;
            if (time.getDoubleValue() >= realTimeInSeconds) {
                return super.transferOutputs(port);
            } else {
                // The current real time is greater than the current
                // model time of the environment. Schedule the production
                // of outputs at the real time.
                time = new Time(this, realTimeInSeconds);
                LinkedList<QueuedToken> outputTokens = new LinkedList<QueuedToken>();
                for (int i = 0; i < port.getWidth(); i++) {
                    try {
                        if (port.isKnownInside(i)) {
                            if (port.hasTokenInside(i)) {
                                Token token = port.getInside(i);
                                outputTokens.add(new QueuedToken(port, i, token));
                                if (RealTimeComposite.this._debugging) {
                                    RealTimeComposite.this._debug(
                                            getName(), "transferring output from "
                                            + port.getName()
                                            + " with value "
                                            + token);
                                }
                            }
                        }
                    } catch (NoTokenException ex) {
                        // this shouldn't happen.
                        throw new InternalErrorException(this, ex, null);
                    }
                }
                if (outputTokens.size() > 0) {
                    OutputFrame frame = new OutputFrame(time, outputTokens);
                    _outputFrames.add(frame);
                    fireAt((Actor)getContainer(), time);
                }
                return false;
            }
        }

        /** Override the base class to wait until the associated thread
         *  terminates and then call super.wrapup().
         *  @exception IllegalActionException If the wrapup() method of
         *   one of the associated actors throws it.
         */
        public void wrapup() throws IllegalActionException {
            if (_delayValue != 0) {
                try {
                    if (RealTimeComposite.this._debugging) {
                        RealTimeComposite.this._debug("Waiting for associated thread to stop.");
                    }
                    _thread.join();
                    if (RealTimeComposite.this._debugging) {
                        RealTimeComposite.this._debug("Associated thread has stopped.");
                    }
                } catch (InterruptedException e) {
                    // Ignore.
                }
            }
            super.wrapup();
        }

        //////////////////////////////////////////////////////////////
        ////                   private variables                  ////
        
        /** List of input events in the current iteration. */
        private List<QueuedToken> _inputTokens;

        /** The thread that executes the contained actors. */
        private Thread _thread;
    }

    ///////////////////////////////////////////////////////////////////
    //// RealTimeThread

    /** This the thread that executed the actors.
     */
    private class RealTimeThread extends Thread {
        public RealTimeThread() {
            super("RealTimeThread");
        }
        public void run() {
            while (!_stopRequested) {
                try {
                    if (RealTimeComposite.this._debugging) {
                        RealTimeComposite.this._debug(
                                "---- Waiting for inputs in the associated thread.");
                    }
                    InputFrame frame = _inputFrames.take();
                    if (frame.tokens == null) {
                        // Recognize a "stop frame" and exit the thread.
                        if (RealTimeComposite.this._debugging) {
                            RealTimeComposite.this._debug(
                                    "---- Read a stop frame in associated thread.");
                        }
                        break;
                    }
                    if (RealTimeComposite.this._debugging) {
                        RealTimeComposite.this._debug(
                                "---- Reading input tokens in associated thread: "
                                + frame.tokens);
                    }
                    for (QueuedToken token : frame.tokens) {
                        if (token.channel < token.port.getWidthInside()) {
                            token.port.sendInside(token.channel, token.token);
                        }
                    }
                    if (!fireContainedActors()) {
                        // postfire() of the contained actors returns false.
                        break;
                    }
                } catch (InterruptedException e) {
                    // Exit the thread.
                    break;
                } catch (IllegalActionException ex) {
                    MessageHandler.error("Error in real-time thread.", ex);
                }
            }
        }
    }
}
