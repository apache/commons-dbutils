
package dbutilspro;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;

import com.github.drinkjava2.dbutilspro.DbPro;
import com.github.drinkjava2.jbeanbox.AopAround;
import com.github.drinkjava2.jbeanbox.BeanBox;
import com.github.drinkjava2.jtransactions.ConnectionManager;
import com.github.drinkjava2.jtransactions.tinytx.TinyTx;
import com.github.drinkjava2.jtransactions.tinytx.TinyTxConnectionManager;

import dbutilspro.DataSourceConfig.DataSourceBox;

/**
 * This is to test TinyTx Declarative Transaction
 *
 * @author Yong Zhu
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class TxDemo {
	private static Class<?> tx = TinyTx.class;
	private static ConnectionManager cm = TinyTxConnectionManager.instance();
	// private static Class<?> tx = SpringTx.class;
	// private static ConnectionManager cm =
	// SpringTxConnectionManager.instance();

	public static class TxBox extends BeanBox {
		{
			this.setConstructor(tx, BeanBox.getBean(DataSourceBox.class), Connection.TRANSACTION_READ_COMMITTED);
		}
	}

	DbPro dbpro = new DbPro((DataSource) BeanBox.getBean(DataSourceBox.class), cm);

	@AopAround(TxBox.class)
	public void tx_Insert1() {
		dbpro.nExecute("insert into users (id) values(?)", 123);
		Assert.assertEquals(1L, dbpro.nQueryForObject("select count(*) from users"));
	}

	@AopAround(TxBox.class)
	public void tx_Insert2() {
		dbpro.nExecute("insert into users (id) values(?)", 456);
		Assert.assertEquals(2L, dbpro.nQueryForObject("select count(*) from users"));
		System.out.println("Now have 2 records in users table, but will roll back to 1");
		System.out.println(1 / 0);
	}

	@Test
	public void doTest() {
		System.out.println("============Testing: TxDemo============");
		TxDemo tester = BeanBox.getBean(TxDemo.class);

		try {
			dbpro.nExecute("drop table users");
		} catch (Exception e) {
		}
		dbpro.nExecute("create table users (id varchar(40))engine=InnoDB");
		Assert.assertEquals(0L, dbpro.nQueryForObject("select count(*) from users"));

		try {
			tester.tx_Insert1();// this one inserted 1 record
			tester.tx_Insert2();// this one did not insert, roll back
		} catch (Exception e) {
			System.out.println("div/0 exception found, tx_Insert2 should roll back");
		}
		Assert.assertEquals(1L, dbpro.nQueryForObject("select count(*) from users"));
		BeanBox.defaultContext.close();// Release DataSource Pool
	}

}