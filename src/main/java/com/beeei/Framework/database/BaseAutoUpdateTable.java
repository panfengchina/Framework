package com.beeei.Framework.database;

import com.beeei.Framework.common.BaseReflect;
import com.beeei.Framework.exception.BaseErrorCodeEnum;
import com.beeei.Framework.exception.BaseException;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BaseAutoUpdateTable {
    private final static String columnName = "COLUMN_NAME";
    private final static String columnType = "COLUMN_TYPE";
    private final static String columnDefault = "COLUMN_DEFAULT";
    private final static String isNullable = "IS_NULLABLE";
    private final static String columnComment = "COLUMN_COMMENT";

    private static String pack = "com.beeei.origin.entity";

    public static void autoUpdateTable(SqlSessionFactory sqlSessionFactory) {
        try {
            Set<Class<?>> classes = BaseReflect.getAllPackageClasses(pack);
            Pattern tableNamePat = Pattern.compile("^[a-zA-Z0-9_]+$");
            SqlSession sqlSession = sqlSessionFactory.openSession();
            Connection connection = sqlSession.getConnection();
            List<String> listString = new ArrayList<>();
            for (Class<?> clas : classes) {

                BaseTable table = clas.getAnnotation(BaseTable.class);
                if (null == table) {
                    continue;
                }
                Matcher tableNameMat = tableNamePat.matcher(table.name());
                if (!tableNameMat.matches()) {
                    throw new BaseException(BaseErrorCodeEnum.SYSTEM_ERROR_TABLE_NAME);
                }
                BaseIndex[] bi = table.indexes();
                Set<Field> fields = BaseReflect.getAllFields(clas);
                List<BaseColumnDetails> baseColumnDetailsListField = new ArrayList<>();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(BaseColumn.class)) {
                        BaseColumn column = field.getAnnotation(BaseColumn.class);
                        baseColumnDetailsListField
                                .add(new BaseColumnDetails(column.columnName(), column.columnType().toUpperCase(),
                                        column.columnDefault(), column.isNullable(), column.columnComment()));
                    }
                }
                StringBuffer s = new StringBuffer("SELECT ");
                s.append(columnName).append(",").append(columnType).append(",").append(columnDefault).append(",");
                s.append(isNullable).append(",").append(columnComment)
                        .append(" FROM INFORMATION_SCHEMA.COLUMNS T WHERE LOWER(T.TABLE_NAME) ='");
                s.append(table.name()).append("'").append(";");
                PreparedStatement preparedStatement = (PreparedStatement) connection.prepareStatement(s.toString(),
                        java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    List<BaseColumnDetails> baseColumnDetailsList = new ArrayList<>();
                    do {
                        baseColumnDetailsList.add(new BaseColumnDetails(rs.getString(columnName),
                                rs.getString(columnType), rs.getString(columnDefault),
                                rs.getString(isNullable).equals("YES") ? true : false, rs.getString(columnComment)));
                    } while (rs.next());
                    Map<String, BaseColumnDetails> mapField = baseColumnDetailsList.stream().collect(
                            Collectors.toMap(BaseColumnDetails::getColumnName, BaseColumnDetails -> BaseColumnDetails));
                    Map<String, BaseColumnDetails> mapList = baseColumnDetailsListField.stream().collect(
                            Collectors.toMap(BaseColumnDetails::getColumnName, BaseColumnDetails -> BaseColumnDetails));

                    StringBuffer sbc = new StringBuffer("ALTER TABLE " + table.name());
                    int updateNum = 0;
                    for (Map.Entry<String, BaseColumnDetails> m : mapList.entrySet()) {
                        if (mapField.containsKey(m.getValue().getColumnName())) {
                            StringBuffer d = new StringBuffer();
                            BaseAutoUpdateTable.baseColumnDetailsToString(d, m.getValue());
                            StringBuffer f = new StringBuffer();
                            BaseAutoUpdateTable.baseColumnDetailsToString(f,
                                    mapField.get(m.getValue().getColumnName()));
                            if (!Objects.equals(d.toString().toLowerCase(), f.toString().toLowerCase())) {
                                sbc.append(" MODIFY ");
                                BaseAutoUpdateTable.baseColumnDetailsToString(sbc, m.getValue());
                                updateNum++;
                            }
                        } else {
                            sbc.append(" ADD ");
                            BaseAutoUpdateTable.baseColumnDetailsToString(sbc, m.getValue());
                            updateNum++;
                        }
                    }
                    if (updateNum > 0) {
                        String sbf = sbc.substring(0, sbc.length() - 1);
                        listString.add(sbf + ";");
                    }
                    PreparedStatement preparedStatementIndex = (PreparedStatement) connection.prepareStatement(
                            "SHOW INDEX FROM " + table.name() + ";", java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,
                            java.sql.ResultSet.CONCUR_READ_ONLY);
                    ResultSet rsIndex = preparedStatementIndex.executeQuery();
                    if (rsIndex.next()) {
                        Map<String, StringBuffer> mapIndex = new HashMap<>();
                        do {
                            if (mapIndex.containsKey(rsIndex.getString("Key_name"))) {
                                StringBuffer sbTemp = mapIndex.get(rsIndex.getString("Key_name"));
                                StringBuffer sdf = new StringBuffer(sbTemp.substring(0, sbTemp.length() - 2));
                                sdf.append(rsIndex.getString("Column_name")).append("),");
                                mapIndex.put(rsIndex.getString("Key_name"), sdf);
                            } else {
                                String tempIn = rsIndex.getString("Non_unique").equals("0") ? "UNIQUE " : "INDEX ";
                                StringBuffer sbti = new StringBuffer(" ADD " + tempIn);
                                sbti.append(rsIndex.getString("Key_name")).append(" (")
                                        .append(rsIndex.getString("Column_name")).append(",),");
                                mapIndex.put(rsIndex.getString("Key_name"), sbti);
                            }
                        } while (rsIndex.next());

                        if (bi != null && bi.length > 0) {
                            StringBuffer sbi = new StringBuffer("ALTER TABLE " + table.name());
                            int indexNum = 0;
                            for (BaseIndex bdex : bi) {
                                if (mapIndex.containsKey(bdex.name())) {
                                    StringBuffer sf = new StringBuffer();
                                    sf.append(" ADD ").append(bdex.unique() ? "UNIQUE " : "INDEX ").append(bdex.name())
                                            .append(" (").append(bdex.columns()).append(")").append(",");
                                    if (!Objects.equals(sf.toString(), mapIndex.get(bdex.name()).toString())) {
                                        listString.add(
                                                "ALTER TABLE " + table.name() + " DROP INDEX " + bdex.name() + ";");
                                        sbi.append(sf);
                                        indexNum++;
                                    }
                                } else {
                                    sbi.append(" ADD ").append(bdex.unique() ? "UNIQUE " : "INDEX ").append(bdex.name())
                                            .append(" (").append(bdex.columns()).append(")").append(",");
                                    indexNum++;
                                }
                            }
                            if (indexNum > 0) {
                                String sbf = sbi.substring(0, sbi.length() - 1);
                                listString.add(sbf + ";");
                            }

                        }

                    } else {
                        if (bi != null && bi.length > 0) {
                            StringBuffer sbi = new StringBuffer("ALTER TABLE " + table.name());
                            for (BaseIndex bdex : bi) {
                                sbi.append(" ADD ").append(bdex.unique() ? "UNIQUE " : "INDEX ").append(bdex.name())
                                        .append(" (").append(bdex.columns()).append(")").append(",");
                            }
                            String sbf = sbi.substring(0, sbi.length() - 1);
                            listString.add(sbf + ";");
                        }
                    }

                } else {
                    StringBuffer sb = new StringBuffer("CREATE TABLE ");
                    sb.append(table.name()).append(" ( ");
                    for (BaseColumnDetails bc : baseColumnDetailsListField) {
                        BaseAutoUpdateTable.baseColumnDetailsToString(sb, bc);
                    }
                    sb.append("PRIMARY KEY (id),");
                    for (BaseIndex b : bi) {
                        if (b.unique()) {
                            sb.append("UNIQUE ");
                        }
                        sb.append("KEY ").append(b.name()).append(" (").append(b.columns()).append("),");
                    }
                    String sbf = sb.substring(0, sb.length() - 1);
                    listString.add(sbf + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
                }
            }
            System.out.println("》》》》》》》》》》》》》》》》开始打印更新表结构SQL！！！");
            listString.forEach(s -> System.out.println(s));

            //TODO 执行sql
            System.out.println("》》》》》》》》》》》》》》》》更新表结构SQL打印完毕！！！");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void baseColumnDetailsToString(StringBuffer sb, BaseColumnDetails bc) {
        StringBuffer defaultValue = new StringBuffer();
        if (Objects.nonNull(bc.getColumnDefault()) && !bc.getColumnDefault().equals("")) {
            defaultValue.append(" DEFAULT '").append(bc.getColumnDefault()).append("'");
        }
        sb.append(bc.getColumnName()).append(" ").append(bc.getColumnType())
                .append(bc.getIsNullable() ? "" : " NOT NULL").append(defaultValue).append(" COMMENT '")
                .append(bc.getColumnComment()).append("',");
    }
}
