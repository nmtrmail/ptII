package ptolemy.actor.corba.CorbaIOUtil;

/**
 * ptolemy/actor/corba/CorbaIOUtil/CorbaIllegalActionExceptionHolder.java .
 * Generated by the IDL-to-Java compiler (portable), version "3.1"
 * from CorbaIO.idl
 * Wednesday, April 16, 2003 5:05:14 PM PDT
 */

public final class CorbaIllegalActionExceptionHolder implements org.omg.CORBA.portable.Streamable
{
    public ptolemy.actor.corba.CorbaIOUtil.CorbaIllegalActionException value = null;

    public CorbaIllegalActionExceptionHolder ()
    {
    }

    public CorbaIllegalActionExceptionHolder (ptolemy.actor.corba.CorbaIOUtil.CorbaIllegalActionException initialValue)
    {
        value = initialValue;
    }

    public void _read (org.omg.CORBA.portable.InputStream i)
    {
        value = ptolemy.actor.corba.CorbaIOUtil.CorbaIllegalActionExceptionHelper.read (i);
    }

    public void _write (org.omg.CORBA.portable.OutputStream o)
    {
        ptolemy.actor.corba.CorbaIOUtil.CorbaIllegalActionExceptionHelper.write (o, value);
    }

    public org.omg.CORBA.TypeCode _type ()
    {
        return ptolemy.actor.corba.CorbaIOUtil.CorbaIllegalActionExceptionHelper.type ();
    }

}
