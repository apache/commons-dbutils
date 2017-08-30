
package dbutilspro;

import static com.github.drinkjava2.dbutilspro.inline.InlineQueryRunner.inline;
import static com.github.drinkjava2.dbutilspro.inline.InlineQueryRunner.inline0;
import static com.github.drinkjava2.dbutilspro.inline.InlineQueryRunner.param;
import static com.github.drinkjava2.dbutilspro.inline.InlineQueryRunner.param0;
import static com.github.drinkjava2.dbutilspro.inline.InlineQueryRunner.question;
import static com.github.drinkjava2.dbutilspro.inline.InlineQueryRunner.question0;
import static com.github.drinkjava2.dbutilspro.inline.InlineQueryRunner.valuesQuesions;
import static com.github.drinkjava2.dbutilspro.tpl.TemplateQueryRunner.put;
import static com.github.drinkjava2.dbutilspro.tpl.TemplateQueryRunner.put0;
import static com.github.drinkjava2.dbutilspro.tpl.TemplateQueryRunner.replace;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.drinkjava2.dbutilspro.DbPro;
import com.github.drinkjava2.jbeanbox.BeanBox;

import dbutilspro.DataSourceConfig.DataSourceBox;

/**
 * This is DbPro usage demo, show different SQL style
 * 
 * <pre>
 * query(Connection, String sql, Object... params):   Original DbUtils methods,  need close Connection and catch SQLException
 * query(String sql, Object... params):   Original DbUtils methods, need catch SQLException
 * nQuery(String sql, Object... params):  normal style, no need catch SQLException
 * iQuery(String... inlineSQLs):  In-line style
 * tQuery(String... sqlTemplate):  SQL Template style
 * </pre>
 * 
 * @author Yong Zhu
 * @since 1.7.0
 */
public class DbProUsageDemo {

	@Before
	public void setupDB() {
		DbPro db = new DbPro((DataSource) BeanBox.getBean(DataSourceBox.class));
		try {
			db.nExecute("drop table users");
		} catch (Exception e) {
		}
		db.nExecute("create table users (name varchar(40), address varchar(40))");
	}

	@After
	public void cleanUp() {
		BeanBox.defaultContext.close();
	}

	public static class User {
		String name;
		String address;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}
	}

	@Test
	public void executeTest() {
		DbPro dbPro = new DbPro((DataSource) BeanBox.getBean(DataSourceBox.class));
		dbPro.setAllowShowSQL(true);
		User user = new User();
		user.setName("Sam");
		user.setAddress("Canada");

		System.out.println("Example#1: DbUtils old style methods, need close connection and catch SQLException");
		Connection conn = null;
		try {
			conn = dbPro.prepareConnection();
			dbPro.execute(conn, "insert into users (name,address) values(?,?)", "Sam", "Canada");
			dbPro.execute(conn, "update users set name=?, address=?", "Sam", "Canada");
			Assert.assertEquals(1L, dbPro.queryForObject(conn, "select count(*) from users where name=? and address=?",
					"Sam", "Canada"));
			dbPro.execute(conn, "delete from users where name=? or address=?", "Sam", "Canada");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				dbPro.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Example#2: DbUtils old style methods, need catch SQLException");
		try {
			dbPro.execute("insert into users (name,address) values(?,?)", "Sam", "Canada");
			dbPro.execute("update users set name=?, address=?", "Sam", "Canada");
			Assert.assertEquals(1L,
					dbPro.queryForObject("select count(*) from users where name=? and address=?", "Sam", "Canada"));
			dbPro.execute("delete from users where name=? or address=?", "Sam", "Canada");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Example#3: nXxxx methods no need catch SQLException");
		dbPro.nExecute("insert into users (name,address) values(?,?)", "Sam", "Canada");
		dbPro.nExecute("update users set name=?, address=?", "Sam", "Canada");
		Assert.assertEquals(1L,
				dbPro.nQueryForObject("select count(*) from users where name=? and address=?", "Sam", "Canada"));
		dbPro.nExecute("delete from users where name=? or address=?", "Sam", "Canada");

		System.out.println("Example#4: iXxxx In-line style methods");
		dbPro.iExecute("insert into users (", //
				" name ,", param0("Sam"), //
				" address ", param("Canada"), //
				") ", valuesQuesions());
		param0("Sam", "Canada");
		dbPro.iExecute("update users set name=?,address=?");
		Assert.assertEquals(1L, dbPro.iQueryForObject("select count(*) from users where name=" + question0("Sam")));
		dbPro.iExecute("delete from users where name=", question0("Sam"), " and address=", question("Canada"));

		System.out.println("Example#5: Another usage of iXxxx inline style");
		dbPro.iExecute("insert into users (", inline0(user, "", ", ") + ") ", valuesQuesions());
		dbPro.iExecute("update users set ", inline0(user, "=?", ", "));
		Assert.assertEquals(1L,
				dbPro.iQueryForObject("select count(*) from users where ", inline0(user, "=?", " and ")));
		dbPro.iExecute(param0(), "delete from users where ", inline(user, "=?", " or "));

		System.out.println("Example#6: tXxxx Sql Template style methods");
		put0("user", user);
		dbPro.tExecute("insert into users (name, address) values(#{user.name},#{user.address})");
		put0("name", "Sam");
		put("addr", "Canada");
		dbPro.tExecute("update users set name=#{name}, address=#{addr}");
		Assert.assertEquals(1L,
				dbPro.tQueryForObject("select count(*) from users where ${col}=#{name} and address=#{addr}",
						put0("name", "Sam"), put("addr", "Canada"), replace("col", "name")));
		dbPro.tExecute("delete from users where name=#{name} or address=#{addr}", put0("name", "Sam"),
				put("addr", "Canada"));
	}

}
