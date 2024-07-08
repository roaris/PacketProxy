package packetproxy.model;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import packetproxy.model.Database.DatabaseMessage;

public class ResenderPackets extends Observable implements Observer {
	private static ResenderPackets instance;

	public static ResenderPackets getInstance() throws Exception {
		if (instance == null) {
			instance = new ResenderPackets();
		}
		return instance;
	}

	private Database database;
	private Dao<ResenderPacket, Integer> dao;

	private ResenderPackets() throws Exception {
		database = Database.getInstance();
		dao = database.createTable(ResenderPacket.class, this);
	}

	public void initTable(boolean restore) throws Exception {
		if (restore) {
			if (!isLatestVersion()) {
				RecreateTable();
			}
		} else {
			database.dropTable(ResenderPacket.class);
			database.createTable(ResenderPacket.class, this);
		}
	}

	public void createResend(ResenderPacket resenderPacket) throws Exception {
		dao.create(resenderPacket);
	}

	public void deleteResends(int resends_index) throws Exception {
		DeleteBuilder<ResenderPacket, Integer> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.where().eq("resends_index", resends_index);
		dao.delete(deleteBuilder.prepare());
	}

	public void deleteResend(int resends_index, int resend_index) throws Exception {
	   DeleteBuilder<ResenderPacket, Integer> deleteBuilder = dao.deleteBuilder();
	   deleteBuilder.where().eq("resends_index", resends_index).and().eq("resend_index", resend_index);
	   dao.delete(deleteBuilder.prepare());
	}

	public List<ResenderPacket> queryAllOrdered() throws Exception {
		return dao.queryBuilder().orderBy("resends_index", true).orderBy("resend_index", true).query();
	}

	@Override
	public void notifyObservers(Object arg) {
		setChanged();
		super.notifyObservers(arg);
		clearChanged();
	}

	@Override
	public void update(Observable o, Object arg) {
		DatabaseMessage message = (DatabaseMessage) arg;
		try {
			switch (message) {
				case PAUSE:
					// TODO ロックを取る
					break;
				case RESUME:
					// TODO ロックを解除
					break;
				case DISCONNECT_NOW:
					break;
				case RECONNECT:
					database = Database.getInstance();
					dao = database.createTable(ResenderPacket.class, this);
					notifyObservers(arg);
					break;
				case RECREATE:
					database = Database.getInstance();
					dao = database.createTable(ResenderPacket.class, this);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isLatestVersion() throws Exception {
		String result = dao.queryRaw("SELECT sql FROM sqlite_master WHERE name='resender_packets'").getFirstResult()[0];
		return result.equals("CREATE TABLE `resender_packets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT , `resends_index` INTEGER , `resend_index` INTEGER , `direction` VARCHAR , `data` BLOB , `listen_port` INTEGER , `client_ip` VARCHAR , `client_port` INTEGER , `server_ip` VARCHAR , `server_port` INTEGER , `server_name` VARCHAR , `use_ssl` BOOLEAN , `encoder_name` VARCHAR , `alpn` VARCHAR , `auto_modified` BOOLEAN , `conn` INTEGER , `group` BIGINT , UNIQUE (`resends_index`,`resend_index`,`direction`) )");
	}

	private void RecreateTable() throws Exception {
		int option = JOptionPane.showConfirmDialog(null,
				"resender_packetsテーブルの形式が更新されているため\n現在のテーブルを削除して再起動しても良いですか？",
				"テーブルの更新",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (option == JOptionPane.YES_OPTION) {
			database.dropTable(ResenderPacket.class);
			dao = database.createTable(ResenderPacket.class, this);
		}
	}
}
