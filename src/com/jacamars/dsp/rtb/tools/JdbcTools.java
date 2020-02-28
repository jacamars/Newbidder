package com.jacamars.dsp.rtb.tools;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class JdbcTools {
	public static ObjectMapper mapper = new ObjectMapper();
	public static final JsonNodeFactory factory = JsonNodeFactory.instance;

	public static synchronized ArrayNode convertToJson(ResultSet rs) throws Exception {
		ArrayNode array = factory.arrayNode();
		ResultSetMetaData rsmd = rs.getMetaData();

		while (rs.next()) {
			ObjectNode child = factory.objectNode();
			int numColumns = rsmd.getColumnCount();
			for (int i = 1; i < numColumns + 1; i++) {
				String column_name = rsmd.getColumnName(i);

				switch (rsmd.getColumnType(i)) {
				case java.sql.Types.BIGINT:
					child.put(column_name, rs.getInt(column_name));
					break;
				case java.sql.Types.BOOLEAN:
					child.put(column_name,rs.getBoolean(column_name));
					break;
				case java.sql.Types.BLOB:
					child.put(column_name, rs.getBlob(column_name)
							.toString().getBytes());
					break;
				case java.sql.Types.DOUBLE:
					child.put(column_name,rs.getDouble(column_name));
					break;
				case java.sql.Types.FLOAT:
					child.put(column_name, rs.getFloat(column_name));
					break;
				case java.sql.Types.INTEGER:
					try {
						child.put(column_name, rs.getInt(column_name));
					} catch (Exception error) {
						System.out.println("NULL POINTER PULLING COLUMN NAME: " + column_name);
						throw error;
					}
					break;		
				case java.sql.Types.NVARCHAR:
					child.put(column_name,rs.getNString(column_name));
					break;
				case java.sql.Types.VARCHAR:
					child.put(column_name,rs.getString(column_name));
					break;
				case java.sql.Types.TINYINT:
					child.put(column_name,rs.getInt(column_name));
					break;
				case java.sql.Types.SMALLINT:
					child.put(column_name,rs.getInt(column_name));
					break;
				case java.sql.Types.DATE:
					child.put(column_name,rs.getDate(column_name)
							.getTime());
					break;
				case java.sql.Types.TIMESTAMP:
					try {
						Timestamp x = rs.getTimestamp(column_name);
						child.put(column_name,x.getTime());
					} catch (Exception e) {       // caused when date is 00:00:00
						child.put(column_name,new Integer(0));
					}
					break;
				case java.sql.Types.DECIMAL:
					BigDecimal decimal = rs.getBigDecimal(column_name);
					child.put(column_name,decimal);
					//System.out.println("------------>" + column_name);
					break;
				case -1:
					child.put(column_name,rs.getString(column_name));
					break;
				case java.sql.Types.CHAR:
					child.put(column_name,rs.getString(column_name));
					break;
				case java.sql.Types.NUMERIC:
					child.put(column_name, rs.getBigDecimal(column_name));
					break;
				case java.sql.Types.ARRAY:
					Array arr = rs.getArray(column_name);
					if (arr == null)
						break;
					
					Object[] o1 = (Object[])arr.getArray();
					var an = mapper.createArrayNode();
					for (Object x : o1) {
						if (x instanceof Integer) {
							an.add(new IntNode((Integer)x));
						} else
						if (x instanceof Double) {
							an.add(new DoubleNode((Double)x));
						} else
						if (x instanceof Float) {
							an.add(new DoubleNode((Float)x));
						} else
						if (x instanceof Long) {
							an.add(new LongNode((Long)x));
						} else if (x instanceof String) {
							an.add(new TextNode((String)x));
						}
					}
					child.set(column_name,an);
					break;
				default:
					if  (rsmd.getColumnTypeName(i).equals("TINYINT")) {
						child.put(column_name,rs.getInt(column_name));
					} else
						System.err.println("Can't convert " + column_name
							+ " type is not supported: "
									+ rsmd.getColumnTypeName(i));
				}
			}
			array.add(child);
		}
		return array;

	}

	public static String jsonToInsert(String topic, Map<String,Object> map) {
		List<String> names = new ArrayList();
		List<Object> vals = new ArrayList();
		String q = "INSERT INTO " + topic + " (";
		map.keySet().forEach(key->{
			names.add(key);
			vals.add(map.get(key));
		});
		for(int i=0;i<vals.size();i++) {
			q += vals.get(i);
			if (i+1 < vals.size())
				q += ",";
		}
		q += ") VALUES()";
		for (int i=0;i<vals.size();i++) {
			Object x = vals.get(i);
			if (x instanceof String) {
				String str = (String) x;
				q += "'" + str + "'";
			} else
				q += x;
			if (i+1 < vals.size())
				q += ",";
		}
		q += ");";
		return q;
	}
	
	public static String toString(ArrayNode nodes) throws Exception {
        return mapper
        .writer()
        .withDefaultPrettyPrinter()
        .writeValueAsString(nodes);
	}
}