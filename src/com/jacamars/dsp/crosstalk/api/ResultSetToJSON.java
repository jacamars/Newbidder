package com.jacamars.dsp.crosstalk.api;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ResultSetToJSON {
	public static ObjectMapper mapper = new ObjectMapper();
	public static final JsonNodeFactory factory = JsonNodeFactory.instance;


	public static synchronized ArrayNode convert(ResultSet rs) throws Exception {
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
	
	public static String toString(ArrayNode nodes) throws Exception {
        return mapper
        .writer()
        .withDefaultPrettyPrinter()
        .writeValueAsString(nodes);
	}
}