package org.easyjava.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.easyjava.file.Dict;
import org.easyjava.util.EOut;
import org.easyjava.util.EString;
import org.easyjava.util.ETool;
import org.easyjava.web.EGlobal;

public class Model {	
	protected static String string ="";
	protected static String relation ="";
	protected static String help ="";
	protected static String type = "";
	protected static String field = "";
	protected static String inverse = "";
	
	public  void createModel(){
		
	    
//	    One2Many
		String[]  comment = new String[3];
		comment[0] = "field=name,string=User,type=Text";
		comment[1] = "field=date,string=Date,type=Date";
		comment[2] = "field=content,string=Comment,type=Text";
		Model.add("comment", comment);
		
		String[]  forum = new String[4];
		forum[0] = "field=name,string=Name,type=Text";
		forum[1] = "field=date,string=Date,type=Date";
		forum[2] = "field=content,string=Content,type=Text";
		forum[3] = "field=comment,string=Comment,type=one2many:comment";
		Model.add("forum", forum);	
	}
	//添加模型数据
	public  static void add(String model_name,String[] fields) {
		String query_table_exist = "select count(*) from pg_class where relname = '"+model_name+"'";
		String sql = "CREATE TABLE "+model_name+"( \n" 
						+ "ID SERIAL PRIMARY KEY  ,\n"  ; 
		String o2m = "";
		Map<String, Map<String,String>> f = new HashMap<>();
		for(String s:fields){
			Map<String, String> field = EString.stringToMap(s);
			string = field.get("string");
			type = field.get("type");
			String ttype = field.get("type");
			switch (ttype) {
			case "Char":
			case "char":
				sql +=  " "+field.get("field")+" CHAR(255)  ,\n" ;
				break;
			case "Text":
			case "text":	
				sql +=  " "+field.get("field")+" TEXT  ,\n" ;
				break;		
			case "Float":
			case "Double":
				sql +=  " "+field.get("field")+" REAL  ,\n" ;
				break;
			case "boolean":
			case "Boolean":
				sql +=  " "+field.get("field")+" BOOLEAN  ,\n" ;
				break;
			default:
				Pattern p = Pattern.compile("(o|O)ne2many:.*");
				if(p.matcher(ttype).matches()){
					String[] t_r = ttype.replace(" ", "").split(":");
					type = t_r[0];
					relation = t_r[1];
					String alter = "alter table "+relation+" add "+model_name+"_id int ";
					try{
						Statement st_alert = DB.connection.createStatement();
						ResultSet co_rs = st_alert.executeQuery("select count(*)from information_schema.columns where table_name = '"+relation+"' and column_name='"+model_name+"_id'");
						co_rs.next();
						if (co_rs.getInt(1)==0){
							EOut.print(alter);
							st_alert.executeUpdate(alter);
						}
						st_alert.close();
					}
					catch(SQLException e){
						
					}
				}
				p = Pattern.compile("(M|m)any2one:.*");
				if(p.matcher(ttype).matches()){
					String[] t_r = ttype.replace(" ", "").split(":");
					type = t_r[0];
					relation = t_r[1];
					inverse = field.get("inverse");
					sql += " "+relation+"_id "+" INT ,\n"; 
				}
				p = Pattern.compile("(M|m)any2many:.*");
				if(p.matcher(ttype).matches()){
					String[] t_r = ttype.replace(" ", "").split(":");
					type = t_r[0];
					relation = t_r[1];
					inverse = field.get("inverse");
					String rel_table_exist = "select count(*) from pg_class where relname = '"+model_name+"_rel'";
					String sql_rel = "CREATE TABLE "+model_name+"_rel(\n\t"+
							model_name+"_id INT,\n\t"+
							relation+"_id INT)";
					 try {
							Statement st = DB.connection.createStatement();
							ResultSet rel_rs = st.executeQuery(rel_table_exist);
							rel_rs.next();
							if (rel_rs.getInt(1)==0){
								EOut.print(sql_rel);
								st.executeUpdate(sql_rel);
							};
							st.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				break;
			}
			Map<String, String> m = new HashMap<>();
			m.put("string", string);
			m.put("type",type);
			m.put("relation", relation);
			m.put("inverse", inverse);
			f.put(field.get("field"), m);
		}
		
		EGlobal.models.put(model_name,f);
		sql += o2m;
		sql = sql.substring(0,sql.length()-2);
		sql += "); ";
		//TODO :仅对postgres有效
		 try {
			Statement st = DB.connection.createStatement();
			ResultSet tb_rs = st.executeQuery(query_table_exist);
			tb_rs.next();
			if (tb_rs.getInt(1)==0){
				EOut.print(sql);
				st.executeUpdate(sql);
			};
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static List<Map<String, String>> read(String table_name,String condition){
		try {
			List<Map<String, String>> res = new ArrayList<>();
			String sql = "select * from "+table_name+" where "+condition +" order by id";
			if(DB.connection==null){
				DATABASE.init();
				DB.init();
			}
			Statement st = DB.connection.createStatement();
			ResultSet rs = st.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()){
				Map<String, String> map = new HashMap<>();
				for(int i = 1 ;i <= rsmd.getColumnCount();i++){
					map.put(rsmd.getColumnName(i), rs.getString(i));
				}
				res.add(map);
			}
			return res;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;		
	}
	
	public static int unlink(String model,int id){
		String sql = "delete from "+model + " where id = "+ id;
		if(DB.connection==null){
			DATABASE.init();
			DB.init();
		}
		try {
			Statement stmt = DB.connection.createStatement();
			EOut.print(sql);
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.err.println("无法删除");
			e.printStackTrace();
		}
		return -1;
		
	}
	
	public static String getString(String model_name,String field){
		if(EGlobal.models.containsKey(model_name)){
			Map<String,Map<String,String>>f = EGlobal.models.get(model_name);
			if(f.containsKey(field)){
				return f.get(field).get("string");
			}
		}
		return null;
	}
	
	public static String getType(String model_name,String field){
		if(EGlobal.models.containsKey(model_name)){
			Map<String,Map<String,String>>f = EGlobal.models.get(model_name);
			if(f.containsKey(field)){
				return f.get(field).get("type");
			}
		}
		return null;
	}
	
	public static String getRelation(String model_name,String field){
		if(EGlobal.models.containsKey(model_name)){
			Map<String,Map<String,String>>f = EGlobal.models.get(model_name);
			if(f.containsKey(field)){
				return f.get(field).get("relation");
			}
		}
		return null;
	}
	
	public static String getInverse(String model_name,String field){
		if(EGlobal.models.containsKey(model_name)){
			Map<String,Map<String,String>>f = EGlobal.models.get(model_name);
			if(f.containsKey(field)){
				return f.get(field).get("inverse");
			}
		}
		return null;
	}
	
	public static int[] getM2mIds(String model_name,String field,String id){
		if(DB.connection==null){
			DATABASE.init();
			DB.init();
		}
		String relation = getRelation(model_name, field);
		String sql = "select "+relation+"_id from "+model_name+"_rel where "+model_name+"_id = "+id;
		try {
			Statement stmt = DB.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			EOut.print(sql);
			ResultSet rs = stmt.executeQuery(sql);
			rs.last();
			int[] ids = new int[rs.getRow()];
			rs.beforeFirst();
			int cr = 0;
			while(rs.next()){
				ids[cr] = rs.getInt(1);
				cr++;
			}
			return ids;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static int[] getO2mId(String model_name,int id,String field){
		if(DB.connection==null){
			DATABASE.init();
			DB.init();
		}
		String relation = getRelation(model_name, field);
		String sql = "select id from "+relation+" where "+model_name+"_id ="+id +" order by id";
		try {
			Statement stmt = DB.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery(sql);
			rs.last();
			int[] ids = new int[rs.getRow()];
			rs.beforeFirst();
			int cr = 0;
			while(rs.next()){
				ids[cr] = rs.getInt(1);
				cr++;
			}
			return ids;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
