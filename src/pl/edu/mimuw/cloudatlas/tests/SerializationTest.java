package pl.edu.mimuw.cloudatlas.tests;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import org.junit.Test;
import org.objenesis.strategy.StdInstantiatorStrategy;
import pl.edu.mimuw.cloudatlas.interpreter.Main;
import pl.edu.mimuw.cloudatlas.model.ValueSet;
import pl.edu.mimuw.cloudatlas.model.ZMI;
import pl.edu.mimuw.cloudatlas.model.serializers.ArraysAsListSerializer;
import pl.edu.mimuw.cloudatlas.model.serializers.ValueSetSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Arrays;

/**
 * Created by tomek on 13.11.15.
 */
public class SerializationTest {
    @Test
    public void test_SerializeThings() throws ParseException, UnknownHostException, FileNotFoundException {
        Kryo kryo = new Kryo();
        ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
        kryo.register(Arrays.asList().getClass(), new ArraysAsListSerializer());
        kryo.register(ValueSet.class, new ValueSetSerializer());
        Log.TRACE();
        Output out = new Output(new FileOutputStream("test.zmi"));
        ZMI z = Main.createTestHierarchy();
        kryo.writeClassAndObject(out, z);
        out.close();
        Input in = new Input(new FileInputStream("test.zmi"));
        ZMI z2 = (ZMI) kryo.readClassAndObject(in);
        in.close();
    }
}
