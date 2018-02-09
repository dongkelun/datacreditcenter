package com.sdhs.creditcenter.idcard.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 根据身份证区号对应的名称将省市县分开 该程序只需执行一次
 * @description
 * @author 		董可伦
 * @time   		2018年2月9日
 *
 */
public class IdCardRegion extends SqlMapClientDaoSupport {
	private static final Logger LOG = LoggerFactory.getLogger(IdCardRegion.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void execute() throws SQLException {
		LOG.info("IdCardRegion start");
		SqlMapClient client = getSqlMapClient();
		List region = client.queryForList("idCardRegion.getRegion");

		HashMap tempMap = new HashMap();
		String code = "";
		String name = "";
		int provinceIndex = -1;
		String province = "";
		String city = "";
		String country = "";
		HashMap paraMap = new HashMap();
		List arrayList = new ArrayList();
		try {
			for (int i = 0; i < region.size(); i++) {
				tempMap = (HashMap) region.get(i);
				code = tempMap.get("CODE").toString();
				name = tempMap.get("NAME").toString();
				name.contains("省");

				if (name.contains("省")) {
					provinceIndex = name.indexOf("省");
					province = name.substring(0, provinceIndex + 1);
					city = getCity(name.substring(provinceIndex + 1, name.length()))[0];
					country = getCity(name.substring(provinceIndex + 1, name.length()))[1];
				} else if (name.contains("自治区")) {
					provinceIndex = name.indexOf("自治区");
					province = name.substring(0, provinceIndex + 3);
					city = getCity(name.substring(provinceIndex + 3, name.length()))[0];
					country = getCity(name.substring(provinceIndex + 3, name.length()))[1];
				} else { //直辖市
					province = "";
					city = getCity(name)[0];
					country = getCity(name)[1];
				}

				paraMap = new HashMap();
				paraMap.put("province", province);
				paraMap.put("city", city);
				paraMap.put("country", country);
				paraMap.put("code", code);
				arrayList.add(paraMap);
			}
			client.startTransaction();
			client.getCurrentConnection().setAutoCommit(false);

			for (int i = 0; i < arrayList.size(); i++) {
				paraMap = (HashMap) arrayList.get(i);
				client.update("idCardRegion.updateRegion", paraMap);

			}
			client.getCurrentConnection().commit();
			client.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			client.endTransaction();
		}

		LOG.info("IdCardRegion end");

	}

	public static String[] getCity(String name) {
		String[] res = new String[2];
		int cityIndex = -1;
		if (name.contains("地区")) {
			cityIndex = name.indexOf("地区");
			res[0] = name.substring(0, cityIndex + 2);
			res[1] = name.substring(cityIndex + 2, name.length());
		} else if (name.contains("自治州")) {
			cityIndex = name.indexOf("自治州");
			res[0] = name.substring(0, cityIndex + 3);
			res[1] = name.substring(cityIndex + 3, name.length());
		} else if (name.contains("市")) {
			cityIndex = name.indexOf("市");
			res[0] = name.substring(0, cityIndex + 1);
			res[1] = name.substring(cityIndex + 1, name.length());
		}else{
			res[0] = "";
			res[1] = name;
		}
		return res;

	}

}
