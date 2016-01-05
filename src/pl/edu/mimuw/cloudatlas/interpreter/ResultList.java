package pl.edu.mimuw.cloudatlas.interpreter;

import pl.edu.mimuw.cloudatlas.model.*;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by tomek on 29.10.15.
 */
public class ResultList extends Result {
    private ValueList value;

    public ResultList(ValueList value) {
        this.value = value;
    }

    public ResultList(Result result){
        if(!result.getValue().getType().isCollection()){
            throw new UnsupportedOperationException();
        }
        TypeCollection tc = (TypeCollection) result.getValue().getType();
        this.value = (ValueList) result.getValue().convertTo(((Type)(new TypeCollection(Type.PrimaryType.LIST, tc.getElementType()))));
    }

    @Override
    protected Result binaryOperationTyped(BinaryOperation operation, ResultSingle right) {
        if(this.value.size() == 0) return new ResultSingle(this.value);
        ArrayList<Value> v = new ArrayList<>();
        for(Value it: this.value){
            v.add(operation.perform(it, right.getValue()));
        }
        return new ResultList(new ValueList(v, TypeCollection.computeElementType(v)));

    }

    @Override
    public Result unaryOperation(UnaryOperation operation) {
        ArrayList<Value> vl = new ArrayList<>();
        for(Value v: this.value){
            vl.add(operation.perform(v));
        }
        return new ResultList(new ValueList(vl, TypeCollection.computeElementType(vl)));
    }

    @Override
    protected Result callMe(BinaryOperation operation, Result left) {
        if(left instanceof ResultSingle){
            Value leftVal = ((ResultSingle)left).getValue();
            ArrayList<Value> vl = new ArrayList<>();
            for(Value v: this.getList()){
                vl.add(operation.perform(leftVal, v));
            }
            return new ResultList(new ValueList(vl, TypeCollection.computeElementType(vl)));


        }
        throw new UnsupportedOperationException("Calling binary operation on "+left.toString() + " "+ this.toString());
    }

    @Override
    public Value getValue() {
        return this.value;
    }

    @Override
    public ValueList getList() {
        return this.value;
    }

    @Override
    public ValueList getColumn() {
        throw new UnsupportedOperationException("Must be ResultColumn to call getColumn.");
    }

    @Override
    public Result filterNulls() {
        return new ResultColumn(Result.filterNullsList(this.value));
    }

    @Override
    public Result first(int size) {
        return new ResultSingle(Result.firstList(value, size));
    }

    @Override
    public Result last(int size) {
        return new ResultSingle(Result.lastList(value, size));
    }

    @Override
    public Result random(int size) {
        return new ResultSingle(Result.randomList(this.value, this.value.size()));
    }

    @Override
    public Result convertTo(Type to) {
        ArrayList<Value> vl = new ArrayList<>();
        for(Value it: this.value){
            vl.add(it.convertTo(to));
        }
        return new ResultList(new ValueList(vl, TypeCollection.computeElementType(vl)));
    }

    @Override
    public ResultSingle isNull() {
        if(this.value == null) return new ResultSingle(new ValueBoolean(true));
        Iterator<Value> it = this.value.iterator();
        while(it.hasNext()){
            if(!it.next().isNull()){
                return new ResultSingle(new ValueBoolean(false));
            }
        }
        return new ResultSingle(new ValueBoolean(true));
    }

    @Override
    public Type getType() {
        return this.value.getType();
    }
}
