package ptolemy.caltrop.ddi;

import caltrop.interpreter.Context;
import caltrop.interpreter.InputPort;
import caltrop.interpreter.SingleInputPort;
import caltrop.interpreter.SingleOutputPort;
import caltrop.interpreter.ast.Action;
import caltrop.interpreter.ast.Actor;
import caltrop.interpreter.ast.PortDecl;
import caltrop.interpreter.environment.Environment;
import ptolemy.actor.TypedIOPort;
import ptolemy.caltrop.actors.CalInterpreter;
import ptolemy.caltrop.ddi.util.DataflowActorInterpreter;
import ptolemy.kernel.util.IllegalActionException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Dataflow extends AbstractDDI implements DDI {

    public Dataflow(CalInterpreter ptActor, Actor actor, Context context, Environment env) {
        _ptActor = ptActor;
        _actor = actor;
        _context = context;
        _env = env;
        _inputPorts = createPortMap(_actor.getInputPorts(), true);
        _outputPorts = createPortMap(_actor.getOutputPorts(), false);
        _actorInterpreter = new DataflowActorInterpreter(_actor, _context, _env, _inputPorts, _outputPorts);
    }

    private Map createPortMap(PortDecl [] ports, boolean isInput) {
        Map portMap = new HashMap();
        for (int i = 0; i < ports.length; i++) {
            String name = ports[i].getName();
            TypedIOPort port = (TypedIOPort) _ptActor.getPort(name);
            if (isInput) {
                portMap.put(name, new SingleInputPort(name, new DFInputChannel(port, 0)));
            } else {
                portMap.put(name, new SingleOutputPort(name, new DFOutputChannel(port, 0)));
            }
        }
        return portMap;
    }

    private CalInterpreter _ptActor;
    private Actor _actor;
    private Context _context;
    private Environment _env;
    private DataflowActorInterpreter _actorInterpreter;
    private Map _inputPorts;
    private Map _outputPorts;


    public boolean isLegalActor() {
        return true;
    }

    public void setupActor() {
    }

    public String getName() {
        return "Default";
    }

    /**
     * Executes the selected action on the first {@link #fire() fire()} call. On successive calls,
     * it rolls back previous
     * state changes, selects a new action and executes it.
     * <p>
     * <b>Note: Is this correct behavior? What is the contract between the result of prefire() and successive
     * calls to fire()?</b>
     *
     * @throws IllegalActionException  If an error occures during the interpretation of the action.
     */
    public void fire() throws IllegalActionException {
        // FIXMELATER: state transition and potentially rollback
        try {
            if (_actorInterpreter.currentAction() == null) {
                // This point is reached iff this is not the first fire() call of this iteration.
                // Hence we could put rollback work here.
                _selectAction();
            }
            if (_actorInterpreter.currentAction() != null) {
                _actorInterpreter.actionStep();
                _actorInterpreter.actionComputeOutputs();
                _actorInterpreter.actionClear();
                _clearInputChannels();
            }
        } catch (Exception e) {
            throw new IllegalActionException("Could not fire CAL actor '" + _actor.getName() + "': " + e.getMessage());
        }
    }
    /**
     * _selectAction picks an action for which the actor interpreter evaluates the guard to true. Note that this does
     * not necessarily mean that <em>all</em> preconditions for firing are satisfied---the amount of "prechecking"
     * depends on the model of computation ddi. (FIXMELATER)
     *
     * @return The action number that was selected, a value <0 if no action was selected.
     * */

    private int  _selectAction() {
        Action [] actions = _actor.getActions();
        for (int i = 0; i < actions.length; i++) {
            _actorInterpreter.actionSetup(actions[i]);   // Note: could we perhaps reuse environment?
            if (_actorInterpreter.actionEvaluatePrecondition()) {
                return i;
            } else {
                _actorInterpreter.actionClear();
            }
        }
        return -1;
    }

    private int  _selectInitializer() {
        Action [] actions = _actor.getInitializers();
        for (int i = 0; i < actions.length; i++) {
            _actorInterpreter.actionSetup(actions[i]);   // Note: could we perhaps reuse environment?
            if (_actorInterpreter.actionEvaluatePrecondition()) {
                return i;
            } else {
                _actorInterpreter.actionClear();
            }
        }
        return -1;
    }


    public void initialize() throws IllegalActionException {
        _clearInputChannels();
        try {
            _selectInitializer();
        } catch (Exception e) {
            throw new IllegalActionException("Error during initializer selection in actor '"
                    + _actor.getName() + "': " + e.getMessage());
        }
        try {
            if (_actorInterpreter.currentAction() != null) {
                _actorInterpreter.actionStep();
                _actorInterpreter.actionComputeOutputs();
                _actorInterpreter.actionClear();
            }
        } catch (Exception e) {
            throw new IllegalActionException("Could not fire initializer in CAL actor '"
                    + _actor.getName() + "': " + e.getMessage());
        }
    }

    private void  _clearInputChannels() {
        for (Iterator iterator = _inputPorts.values().iterator(); iterator.hasNext();) {
            InputPort inputPort = (InputPort) iterator.next();
            for (int i = 0; i < inputPort.width(); i++) {
                DFInputChannel c = (DFInputChannel)inputPort.getChannel(i);
                c.reset();
            }
        }
    }

    public boolean postfire() throws IllegalActionException {
        return false;
    }

    /**
     * Select a firable action among the actions of the actor, if possible.
     *
     * @return True, if an action could be selected.
     * @throws IllegalActionException If an error occurred during the action selection.
     *
     * @see SDF#_selectAction
     */
    public boolean prefire() throws IllegalActionException {
        try {
            _selectAction();
            if (_actorInterpreter.currentAction() != null)
                return true;
            else
                return _ptActor.superPrefire();
        } catch (Exception e) {
            throw new IllegalActionException("Error during action selection in actor '" + _actor.getName() + "': " + e.getMessage());
        }
    }

    public void preinitialize() throws IllegalActionException {
    }

}


