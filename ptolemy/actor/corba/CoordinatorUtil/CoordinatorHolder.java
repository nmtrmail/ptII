package ptolemy.actor.corba.CoordinatorUtil;

/**
* ptolemy/actor/corba/CoordinatorUtil/CoordinatorHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from Coordinator.idl
*
*/


/* A CORBA compatible interface for a coordinator.
         */
public final class CoordinatorHolder implements org.omg.CORBA.portable.Streamable
{
  public ptolemy.actor.corba.CoordinatorUtil.Coordinator value = null;

  public CoordinatorHolder ()
  {
  }

  public CoordinatorHolder (ptolemy.actor.corba.CoordinatorUtil.Coordinator initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = ptolemy.actor.corba.CoordinatorUtil.CoordinatorHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    ptolemy.actor.corba.CoordinatorUtil.CoordinatorHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return ptolemy.actor.corba.CoordinatorUtil.CoordinatorHelper.type ();
  }

}
