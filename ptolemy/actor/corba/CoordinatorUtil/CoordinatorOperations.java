package ptolemy.actor.corba.CoordinatorUtil;


/**
 * ptolemy/actor/corba/CoordinatorUtil/CoordinatorOperations.java .
 * Generated by the IDL-to-Java compiler (portable), version "3.1"
 * from Coordinator.idl
 *
 */


/* A CORBA compatible interface for a coordinator.
 */
public interface CoordinatorOperations
{

    /* this method is intended to be called remotely by the client
     * to register with the coordinator.
     */
    void register (String clientName, ptolemy.actor.corba.CoordinatorUtil.Client clientRef) throws ptolemy.actor.corba.CoordinatorUtil.CorbaIllegalActionException;

    /* this method is intended to be called remotely by the client,
     * so that data can be delived back over the network.
     */
    void result (String clientName, org.omg.CORBA.Any data) throws ptolemy.actor.corba.CoordinatorUtil.CorbaIllegalActionException;

    /* this method is intended to be called remotely by the client
     * to unregister with this when it leaves.
     */
    void unregister (String consumerName) throws ptolemy.actor.corba.CoordinatorUtil.CorbaIllegalActionException;
} // interface CoordinatorOperations
