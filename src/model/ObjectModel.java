/*
 * To change this license header, choose License Headethis.rs in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import customAnnotation.AutoGenerate;
import customAnnotation.MyId;
import entity.Filter;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author dongvu
 */
public class ObjectModel<T> {

    private final Class<T> CLAZZ;
    private final String TABLENAME;

    private Field[] fields;
    private StringBuilder queryBuilder, conditionBuilder;
    private PreparedStatement pstm;
    private ResultSet rs;

    public ObjectModel(Class<T> clazz) {
        this.CLAZZ = clazz;
        this.TABLENAME = clazz.getSimpleName();
    }

    public T getObject(T obj, Filter filter) {
        try {
            obj = CLAZZ.newInstance();
            createConditionByFilter(filter);
            this.queryBuilder = new StringBuilder()
                    .append("SELECT * FROM ")
                    .append(TABLENAME)
                    .append(this.conditionBuilder.toString());
            this.pstm = ConnectionHelper.getInstance().getConnection().prepareStatement(this.queryBuilder.toString());
            this.rs = this.pstm.executeQuery();
            this.fields = obj.getClass().getDeclaredFields();
            if (this.rs.next()) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    objectInitialization(field, obj);
                }
            }
        } catch (SQLException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ObjectModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    public int getCount(T obj, Filter filter) {
        int count = 0;
        try {
            this.queryBuilder = new StringBuilder();
            createConditionByFilter(filter);
            this.queryBuilder.append("SELECT COUNT(*) FROM ").append(this.TABLENAME).append(this.conditionBuilder.toString());
            this.pstm = ConnectionHelper.getInstance().getConnection().prepareStatement(this.queryBuilder.toString());
            this.rs = pstm.executeQuery();
            if (this.rs.next()) {
                count = this.rs.getInt("COUNT(*)");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ObjectModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return count;
    }

    public ArrayList<T> getList(T obj, Filter filter, int limit, int offset) {
        if (obj == null) {
            System.err.println("[GETLIST FAILED] - Object is null !!");
            return new ArrayList<>();
        }
        ArrayList<T> list = new ArrayList<>();
        this.queryBuilder = new StringBuilder();
        createConditionByFilter(filter);
        this.queryBuilder.append("SELECT * FROM ").append(this.TABLENAME).append(this.conditionBuilder.toString());
        if (limit > 0) {
            this.queryBuilder.append(" LIMIT ").append(limit);
        }
        if (offset > 0) {
            this.queryBuilder.append(" OFFSET ").append(offset);
        }
        try {
            this.pstm = ConnectionHelper.getInstance().getConnection().prepareStatement(this.queryBuilder.toString());
            this.rs = this.pstm.executeQuery();
            while (this.rs.next()) {
                try {
                    obj = CLAZZ.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(ObjectModel.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.fields = obj.getClass().getDeclaredFields();
                for (Field field : this.fields) {
                    field.setAccessible(true);
                    try {
                        objectInitialization(field, obj);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        System.out.println(ex.getMessage());
//                        Logger.getLogger(ObjectModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                list.add(obj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ObjectModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public boolean Insert(Object obj) {
        if (obj == null) {
            System.err.println("[INSERT FAILED] - Object is null ");
            return false;
        }
        createTableIfNotExist(obj);
        this.fields = obj.getClass().getDeclaredFields();
        StringBuilder fieldNameBuilder = new StringBuilder();
        StringBuilder fieldValueBuilder = new StringBuilder();
        for (Field field : this.fields) {
            field.setAccessible(true);
            try {
                if (field.get(obj) != null) {
                    if (field.isAnnotationPresent(AutoGenerate.class)) {
                        continue;
                    }
                    fieldNameBuilder.append(field.getName()).append(",");
                    fieldValueBuilder.append("?,");
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(ObjectModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        fieldNameBuilder.setLength(fieldNameBuilder.length() - 1);
        fieldValueBuilder.setLength(fieldValueBuilder.length() - 1);

        this.queryBuilder = new StringBuilder()
                .append("INSERT INTO ")
                .append(this.TABLENAME)
                .append(" (")
                .append(fieldNameBuilder.toString())
                .append(")")
                .append(" VALUES (")
                .append(fieldValueBuilder.toString())
                .append(");");

        try {
            this.pstm = ConnectionHelper.getInstance().getConnection().prepareStatement(this.queryBuilder.toString());
            int position = 1;
            for (Field field : this.fields) {
                if (field.isAnnotationPresent(AutoGenerate.class)) {
                    continue;
                }
                field.setAccessible(true);
                if (field.get(obj) != null) {
                    System.out.println("====== Type Matching =====");
                    typeMatching(field, obj, position);
                    position++;
                }
            }
            if (!this.pstm.execute()) {
                System.err.println(">>>>> Insert success <<<<<");

                return true;
            } else {
                System.err.println("> [INSERT FAILED]");
            }
        } catch (IllegalArgumentException e1) {
            System.err.println(e1.getMessage());
        } catch (SQLException e2) {
            System.err.println("> [INSERT FAILED] - Object is exist");
        } catch (IllegalAccessException e3) {
            System.err.println(e3.getMessage());
        }

        return false;
    }

    public boolean Update(Object obj) {
        if (obj == null) {
            System.err.println("[UPDATE FAILED] - Object is null ");
            return false;
        }
        try {
            this.queryBuilder = new StringBuilder();
            this.queryBuilder.append("UPDATE ")
                    .append(this.TABLENAME)
                    .append(" SET ");
            this.fields = obj.getClass().getDeclaredFields();
            for (Field field : this.fields) {
                field.setAccessible(true);
                if ((field.get(obj) != null)) {
                    if (field.isAnnotationPresent(MyId.class)) {
                        createConditionById(obj, field);
                        continue;
                    }
                    this.queryBuilder.append(field.getName())
                            .append(" = ");
                    if (field.getType().getSimpleName().equals("String")) {
                        this.queryBuilder.append("'");
                        String[] stringSpl = null;
                        StringBuilder fixSimbol = new StringBuilder();

                        if (field.get(obj).toString().contains("\'")) {
                            stringSpl = field.get(obj).toString().split("\'");
                            for (String string : stringSpl) {
                                fixSimbol.append(string);
                            }
                            this.queryBuilder.append(fixSimbol.toString());
                        } else {
                            this.queryBuilder.append(field.get(obj));
                        }
                        this.queryBuilder.append("'");
                    } else {
                        this.queryBuilder.append(field.get(obj));
                    }
                    this.queryBuilder.append(",");
                }
            }
            this.queryBuilder.setLength(this.queryBuilder.length() - 1);
            this.queryBuilder.append(" WHERE ")
                    .append(this.conditionBuilder.toString());
            this.pstm = ConnectionHelper.getInstance().getConnection().prepareStatement(this.queryBuilder.toString());
            if (pstm.executeUpdate(this.queryBuilder.toString()) > 0) {
                System.err.println(">>>>> Update success <<<<<");

                return true;
            } else {
                System.err.println("> [UPDATE FAILED]");
            }
        } catch (SQLException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(ObjectModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public boolean Delete(Object obj) {
        if (obj == null) {
            System.err.println("[DELETE FAILED] - Object is null ");
            return false;
        }
        try {
            this.fields = obj.getClass().getDeclaredFields();
            this.queryBuilder = new StringBuilder();
            this.queryBuilder.append("UPDATE ")
                    .append(this.TABLENAME)
                    .append(" SET status = -1 WHERE ");
            for (Field field : this.fields) {
                if (field.isAnnotationPresent(MyId.class)) {
                    createConditionById(obj, field);
                }
            }
            this.queryBuilder.append(this.conditionBuilder.toString());
            this.pstm = ConnectionHelper.getInstance().getConnection().prepareStatement(this.queryBuilder.toString());
            if (pstm.executeUpdate(this.queryBuilder.toString()) > 0) {
                System.err.println(">>>>> Delete success <<<<<");

                return true;
            } else {
                System.err.println("> [DELETE FAILED] - Object does not exist !");
            }
        } catch (SQLException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(ObjectModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    private void objectInitialization(Field field, Object obj) throws SQLException, IllegalArgumentException, IllegalAccessException {
        switch (field.getType().getSimpleName()) {
            case "String":
                field.set(obj, this.rs.getString(field.getName()));
                break;
            case "int":
                field.setInt(obj, this.rs.getInt(field.getName()));
                break;
            case "long":
                field.setLong(obj, this.rs.getLong(field.getName()));
                break;
            case "double":
                field.setDouble(obj, this.rs.getDouble(field.getName()));
                break;
            case "boolean":
                field.setBoolean(obj, this.rs.getBoolean(field.getName()));
                break;
            case "byte":
                field.setByte(obj, this.rs.getByte(field.getName()));
                break;
            case "char":
                // Need to fix.
//                field.setChar(obj, this.rs.getCharacterStream(field.getName()));
                break;
            case "float":
                field.setFloat(obj, this.rs.getFloat(field.getName()));
                break;
            case "short":
                field.setShort(obj, this.rs.getShort(field.getName()));
                break;
            default:
                System.err.println("Column " + field.getName() + " is null");
        }
    }

    private void createTableIfNotExist(Object obj) {
        try {

            this.pstm = ConnectionHelper.getInstance().getConnection().prepareStatement("SHOW TABLES LIKE '" + this.TABLENAME + "'");
            this.rs = this.pstm.executeQuery();
            if (!this.rs.next()) {
                System.out.println("> Table is not exist\n> Creating table .....");
                this.fields = obj.getClass().getDeclaredFields();
                this.queryBuilder = new StringBuilder().append("CREATE TABLE ").append(this.TABLENAME).append(" (");
                for (Field field : this.fields) {
                    this.queryBuilder.append(field.getName());
                    this.queryBuilder.append(" ");
                    switch (field.getType().getSimpleName()) {
                        case "String":
                            if (field.isAnnotationPresent(MyId.class)) {
                                this.queryBuilder.append("varchar(255) CHARACTER SET utf8");
                            } else {
                                this.queryBuilder.append("text CHARACTER SET utf8");
                            }
                            break;
                        case "int":
                            this.queryBuilder.append("int");
                            break;
                        case "long":
                            this.queryBuilder.append("bigint");
                            break;
                        // need add more case for more type here.
                    }
                    if (field.isAnnotationPresent(MyId.class)) {
                        this.queryBuilder.append(" NOT NULL PRIMARY KEY");
                    }
                    if (field.isAnnotationPresent(AutoGenerate.class)) {
                        this.queryBuilder.append(" AUTO_INCREMENT");
                    }
                    this.queryBuilder.append(",");
                }
                this.queryBuilder.setLength(this.queryBuilder.length() - 1);
                this.queryBuilder.append(");");

                if (!pstm.execute(this.queryBuilder.toString())) {
                    System.out.println("> Created table named " + this.TABLENAME);
                } else {
                    System.err.println("> [CREATE TABLE FAILED]");
                }
            }
        } catch (SQLException ex) {
            System.err.println("> [CREATE TABLE FAILED] - Table named " + this.TABLENAME + " is early exist !");
        }
    }

    private void typeMatching(Field field, Object obj, int position) throws IllegalArgumentException, IllegalAccessException, SQLException {
        switch (field.getType().getSimpleName()) {
            case "String":
//                StringBuilder fixSimbol = new StringBuilder();
//                try {
//                    if (field.get(obj).toString().contains("\"")) {
//                        String[] stringSpl = field.get(obj).toString().split("\"");
//                        for (String string : stringSpl) {
//                            fixSimbol.append(string);
//                        }
//                    } else {
//                        fixSimbol.append(field.get(obj));
//                    }
                this.pstm.setString(position, field.get(obj).toString());

//                } catch (IllegalAccessException | IllegalArgumentException | SQLException e) {
//                }
                break;
            case "int":
                this.pstm.setInt(position, Integer.parseInt(field.get(obj).toString()));
                break;
            case "long":
                this.pstm.setLong(position, Long.parseLong(field.get(obj).toString()));
                break;
            case "double":
                this.pstm.setDouble(position, Double.parseDouble(field.get(obj).toString()));
                break;
            // need add more case for more type here.
        }
    }

    public void createConditionByFilter(Filter filter) {
        if (filter == null) {
            System.err.println("Filter is null");
            return;
        }
        this.conditionBuilder = new StringBuilder().append(" WHERE ");
        for (Map.Entry<String, Filter.Conditions> entry : filter.getField().entrySet()) {
            this.conditionBuilder.append(entry.getKey())
                    .append(" ")
                    .append(entry.getValue().getCompare().getValue())
                    .append(" ");
            try {
                Double.valueOf(entry.getValue().getCompareValue());
                this.conditionBuilder.append(entry.getValue().getCompareValue());
            } catch (NumberFormatException e) {
                this.conditionBuilder.append("'")
                        .append(entry.getValue().getCompareValue())
                        .append("'");
            }
            this.conditionBuilder.append(" AND ");
        }
        this.conditionBuilder.setLength(this.conditionBuilder.length() - 5);
    }

    public void createConditionById(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
        this.conditionBuilder = new StringBuilder();
        field.setAccessible(true);
        this.conditionBuilder.append(field.getName())
                .append(" = ");
        if (field.getType().getSimpleName().equals("String")) {
            this.conditionBuilder.append("'")
                    .append(field.get(obj))
                    .append("'");
        } else {
            this.conditionBuilder.append(field.get(obj));
        }
    }

}
