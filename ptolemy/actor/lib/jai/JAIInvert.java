package ptolemy.actor.lib.jai;

import ptolemy.actor.lib.Transformer;
import ptolemy.data.type.BaseType;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.*;

import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

public class JAIInvert extends Transformer {
    
    public JAIInvert(CompositeEntity container, String name)
            throws IllegalActionException, NameDuplicationException {
        super(container, name);
        input.setTypeEquals(BaseType.OBJECT);
        output.setTypeEquals(BaseType.OBJECT);
    }

    public void fire() throws IllegalActionException {
        super.fire();
        _parameters = new ParameterBlock();
        JAIImageToken jaiImageToken = (JAIImageToken) input.get(0);
        RenderedOp oldImage = jaiImageToken.getValue();
        _parameters.addSource(oldImage);
        RenderedOp newImage = JAI.create("invert", _parameters);
        output.send(0, new JAIImageToken(newImage));
    }
    
    private ParameterBlock _parameters;
}
