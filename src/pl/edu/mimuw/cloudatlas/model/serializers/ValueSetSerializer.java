package pl.edu.mimuw.cloudatlas.model.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import pl.edu.mimuw.cloudatlas.model.TypeCollection;
import pl.edu.mimuw.cloudatlas.model.Value;
import pl.edu.mimuw.cloudatlas.model.ValueSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by tomek on 21.11.15.
 */
public class ValueSetSerializer extends Serializer<ValueSet> {

    @Override
    public ValueSet read(Kryo kryo, Input input, Class type) {
        TypeCollection t  =(TypeCollection)kryo.readClassAndObject(input);
        Set<Value> s = (HashSet<Value>)kryo.readClassAndObject(input);
        return new ValueSet(s, t.getElementType());
    }

    @Override
    public void write(Kryo kryo, Output output, ValueSet object) {
        kryo.writeClassAndObject(output, object.getType());
        HashSet<Value> hs = new HashSet<>();
        hs.addAll(object.getValue());
        kryo.writeClassAndObject(output, hs);
    }
}
