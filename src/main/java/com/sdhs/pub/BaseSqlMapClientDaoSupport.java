package com.sdhs.pub;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * 选择数据源工具类
 * @description
 * @author 		董可伦
 * @time   		2018年2月9日
 *
 */
public class BaseSqlMapClientDaoSupport extends SqlMapClientDaoSupport implements ApplicationContextAware {
	 protected ApplicationContext context;
	 protected ApplicationContext getContext() {
	  // return WebApplicationContextUtils
	  // .getWebApplicationContext(ServletActionContext
	  // .getServletContext());
	  return context;
	 }
	 public void choseSqlClient(String name) {
	  SqlMapClient client = (SqlMapClient) getContext().getBean(name);
	  setSqlMapClientTemplate(new SqlMapClientTemplate(client));
	  afterPropertiesSet();
	 }
	 public void setApplicationContext(ApplicationContext context) throws BeansException {
	  this.context = context;
	 }
	}
