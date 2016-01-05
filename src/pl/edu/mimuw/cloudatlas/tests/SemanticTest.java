package pl.edu.mimuw.cloudatlas.tests;

import org.junit.Test;
import pl.edu.mimuw.cloudatlas.model.TypePrimitive;
import pl.edu.mimuw.cloudatlas.model.ValueBoolean;
import pl.edu.mimuw.cloudatlas.model.ValueString;

/**
 * Created by tomek on 21.11.15.
 */
public class SemanticTest {
    @Test
    public void testAdd(){
        ValueBoolean vb = new ValueBoolean(true);
        ValueString vs = new ValueString("tru");
        System.out.println(vb.convertTo(TypePrimitive.STRING));
        System.out.println(vs.convertTo(TypePrimitive.BOOLEAN));
    }
}
