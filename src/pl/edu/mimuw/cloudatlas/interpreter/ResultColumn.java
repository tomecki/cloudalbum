package pl.edu.mimuw.cloudatlas.interpreter;

import pl.edu.mimuw.cloudatlas.model.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by tomek on 29.10.15.
 */
public class ResultColumn extends Result {

    private ValueList value;

    public ResultColumn(Value v){
        if(!(v instanceof ValueList)){
            throw new UnsupportedOperationException("Cannot create ResultColumn from type: "+ v.getType().getPrimaryType().name());
        }
        this.value = (ValueList)v;
    }

    @Override
    protected Result binaryOperationTyped(BinaryOperation operation, ResultSingle right) {
        if(this.value.size() == 0) return new ResultSingle(this.value);
        ArrayList<Value> v = new ArrayList<>();
        for(Value it: this.value){
            v.add(operation.perform(it, right.getValue()));
        }
        return new ResultColumn(new ValueList(v, TypeCollection.computeElementType(v)));
    }



    @Override
    public Result unaryOperation(UnaryOperation operation) {
        ArrayList<Value> vl = new ArrayList<>();
        for(Value v: this.value){
            vl.add(operation.perform(v));
        }
        return new ResultColumn(new ValueList(vl, TypeCollection.computeElementType(vl)));
    }

    @Override
    protected Result callMe(BinaryOperation operation, Result left) {
        if(left instanceof ResultColumn){
            ValueList leftList = ((ResultColumn)left).getColumn();

            if(leftList.size() != value.size()){
                throw new UnsupportedOperationException("Binary operator: column sizes should be equal!");
            }
            if(leftList.size() == 0){
                return new ResultColumn(leftList);
            }

            Iterator<Value> myIterator = value.iterator();
            Iterator<Value> leftIterator = ((ResultColumn)left).value.iterator();
            ArrayList<Value> vl = new ArrayList<>();
            while(myIterator.hasNext() && leftIterator.hasNext()){
                Value mineVal = myIterator.next();
                Value leftVal = leftIterator.next();
                vl.add(operation.perform(leftVal, mineVal));
            }
            return new ResultColumn(new ValueList(vl, TypeCollection.computeElementType(vl)));
        }
        if(left instanceof ResultSingle){
            Value leftVal = ((ResultSingle)left).getValue();
            ArrayList<Value> vl = new ArrayList<>();
            for(Value v: this.getColumn()){
                vl.add(operation.perform(leftVal, v));
            }
            return new ResultColumn(new ValueList(vl, TypeCollection.computeElementType(vl)));


        }
        throw new NotImplementedException();
    }

    @Override
    public Value getValue() {
        return this.value;
    }

    @Override
    public ValueList getList() {
        throw new UnsupportedOperationException("Must be ResultList to call getList.");
    }

    @Override
    public ValueList getColumn() {
        return this.value;
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
//        return new ResultColumn(value.convertTo(to));
        ArrayList<Value> vl = new ArrayList<>();
        for(Value it: this.value){
            vl.add(it.convertTo(to));
        }
        return new ResultColumn(new ValueList(vl, TypeCollection.computeElementType(vl)));
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
