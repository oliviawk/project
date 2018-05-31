package com.cn.hitec.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

public class IpList {
	@Value("${jdbc.driver}")
	private static String mysqlDriver = "com.mysql.jdbc.Driver";
	@Value("${jdbc.url}")
	// private static String mysqlUrl =
	// "jdbc:mysql://10.30.17.171:3306/pmsc?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true";
	private static String mysqlUrl = "jdbc:mysql://10.20.67.180:3306/cdb?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true";
	@Value("${jdbc.username}")
	private static String mysqlUser = "user";
	@Value("${jdbc.password}")
	private static String mysqlPass = "user171";

	public static List<String> getIpList() {
		List<String> listIp = new ArrayList<String>();
		listIp.add("10.30.16.220");
		listIp.add("10.30.16.223");
		listIp.add("10.30.16.236");
		listIp.add("10.0.74.226");
		listIp.add("120.26.9.109");
		listIp.add("10.30.16.242");
		listIp.add("10.30.16.224");

		// 分钟降水
		listIp.add("10.30.16.249");
		listIp.add("10.30.16.231");
		listIp.add("10.30.16.232");
		listIp.add("10.30.16.234");
		listIp.add("10.30.16.225");
		listIp.add("10.30.16.192");
		return listIp;
	}

	public static List<String> getEventIpList() throws Exception {
		List<String> list = new ArrayList<String>();
		Connection conn = null;
		StringBuffer sqlbuffer1 = new StringBuffer();
		StringBuffer sqlbuffer2 = new StringBuffer();
		List<String> listID = new ArrayList<String>();
		List<String> listIp = new ArrayList<String>();
		try {
			Class.forName(mysqlDriver); // JDBC驱动程序
			conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPass);
			sqlbuffer1.append(
					"select id,name from data_info where parent_id=(select id from data_info where `name`='基础资源')");
			sqlbuffer2.append("select name,service_type from data_info where parent_id=?");
			Statement stmt = conn.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(sqlbuffer1.toString());

			// rs = stmt.getResultSet();
			while (rs.next()) {
				// System.out.println(rs.getString(1));
				listID.add(rs.getString(1));
			}
			rs.close();
			stmt.close();

			PreparedStatement stmt_ip = conn.prepareStatement(sqlbuffer2.toString());
			Iterator<String> it_ip = listID.iterator();
			while (it_ip.hasNext()) {
				String thisId = it_ip.next();
				stmt_ip.setString(0, thisId);
				ResultSet rs_ip = null;
				rs_ip = stmt_ip.executeQuery();
				StringBuffer buffer_ip = new StringBuffer();
				String thisIp = null;
				while (rs_ip.next()) {
					buffer_ip.append(rs.getString(1));
					buffer_ip.append(",");
					thisIp = rs.getString(2);
				}
				// 删掉末尾的逗号
				buffer_ip.deleteCharAt(buffer_ip.length() - 1);
				listIp.add(thisIp + "丨" + buffer_ip.toString());
			}

			conn.close();
			listIp.add("10.30.16.220");
			listIp.add("10.30.16.223");
			listIp.add("10.30.16.236");
			listIp.add("10.0.74.226");
			listIp.add("120.26.9.109");
			listIp.add("10.30.16.242");
			listIp.add("10.30.16.224");

			// 分钟降水
			listIp.add("10.30.16.249");
			listIp.add("10.30.16.231");
			listIp.add("10.30.16.232");
			listIp.add("10.30.16.234");
			listIp.add("10.30.16.225");
			listIp.add("10.30.16.192");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return listIp;
		}
		return listIp;
	}

}
