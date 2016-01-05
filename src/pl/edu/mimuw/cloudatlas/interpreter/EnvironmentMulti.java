package pl.edu.mimuw.cloudatlas.interpreter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tomek on 30.10.15.
 */
public class EnvironmentMulti extends Environment {
    private final List<TableRow> rows;
    private final Table table;
    public EnvironmentMulti(TableRow row, List<String> columns) {
        super(row, columns);
        rows = null;
        table = null;
    }
    public EnvironmentMulti(Table table){
        this.table = table;
        rows = new ArrayList<>();
        Iterator<TableRow> it = table.iterator();
        while(it.hasNext()){
            rows.add(it.next());
        }
        int i = 0;
        for(String c : table.getColumns())
            this.columns.put(c, i++);

    }

    @Override
    public Result getIdent(String ident) {
        return new ResultColumn(this.table.getColumn(ident));
//        List<Value> column = new ArrayList<>();
//        for(TableRow r: rows){
//            try {
//                column.add(r.getIth(columns.get(ident)));
//            } catch(NullPointerException ex){
//                column.add(ValueNull.getInstance());
//            }
//        }
//        if(column.size()>0) {
//            Type type = column.get(0).getType();
//            return new ResultColumn(new ValueList(column, type));
//        } else {
//            return new ResultColumn(ValueNull.getInstance());
//        }
    }
}
