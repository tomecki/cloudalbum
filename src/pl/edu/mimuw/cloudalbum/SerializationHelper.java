package pl.edu.mimuw.cloudalbum;

import com.esotericsoftware.kryo.Kryo;
import org.objenesis.strategy.StdInstantiatorStrategy;
import pl.edu.mimuw.cloudatlas.model.ValueSet;
import pl.edu.mimuw.cloudatlas.model.serializers.ArraysAsListSerializer;
import pl.edu.mimuw.cloudatlas.model.serializers.ValueSetSerializer;

import java.util.Arrays;

/**
 * Created by tomek on 21.12.15.
 */
public class SerializationHelper {
    private static Kryo kryo;
    public static Kryo getKryo() {
        if(kryo==null){
            kryo = new Kryo();
            ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                    .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            kryo.register(Arrays.asList().getClass(), new ArraysAsListSerializer());
            kryo.register(ValueSet.class, new ValueSetSerializer());
        }
        return kryo;
    }
}
