package com.jacamars.dsp.rtb.crawler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.jacamars.dsp.rtb.tools.Env;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum Ads {

	INSTANCE;

	Connection connection;
	Queue<String> queue = new LinkedList<String>();
	protected static final Logger logger = LoggerFactory.getLogger(Ads.class);

	public static void main(String[] args) throws Exception {
		var port = Env.GetEnvironment("port", "8080");
		var host = Env.GetEnvironment("host", "localhost");
		var db = Env.GetEnvironment("db", "");
		var username = Env.GetEnvironment("username", "postgres");
		var password = Env.GetEnvironment("password", "postgres");
		var override = Env.GetEnvironment("override", "false");
		getInstance("jdbc:postgresql://" + host + "/" + db, username, password)
				.initialize(Boolean.parseBoolean(override))
				.webAccess(Integer.parseInt(port))
				.start();

	}

	public static Ads getInstance(String url, String user, String password) throws Exception {
		INSTANCE.connection = connect(url, user, password);
		return INSTANCE;
	}

	public static Ads getInstance() {
		return INSTANCE;
	}

	static Connection connect(String url, String user, String password) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);

			if (conn != null) {
				logger.info("Connected to the PostgreSQL server successfully.");
			} else {
				logger.error("Failed to make connection!");
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			System.exit(0);
		}

		return conn;
	}

	public void offer(String url) {
		queue.offer(url);
	}

	public Connection getConnection() {
		return connection;
	}

	public Ads initialize(boolean override) throws Exception {
		
		GetAds.preload();
		
		final var stmt = connection.createStatement();

		try {
			if (!override)
				stmt.execute("SELECT * FROM adsystem_domain;");
			else
				try {
					stmt.execute(new String(Files.readAllBytes(Paths.get("sql/adstxt.sql")), StandardCharsets.UTF_8));
				} catch (Exception error) {
					throw error;
				}
		} catch (Exception err) {
			stmt.execute(new String(Files.readAllBytes(Paths.get("sql/adstxt.sql")), StandardCharsets.UTF_8));
		}

		Runnable jobs;
		jobs = () -> {
			try {
				List<GetAds> list = new ArrayList<>();
				while (true) {
					if (list.size() < 15) {
						String url = queue.poll();
						if (url != null) {
							if (!pending(list, url)) {
								if (url != null) {
									GetAds a = new GetAds(url, "7");
									list.add(a);
									a.start();
								}
							}
						}
						Thread.sleep(1);
					}
					GetAds done = null;
					while ((done = GetAds.getCompleted()) != null) {
						list.remove(done);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Thread nthread = new Thread(jobs);
		nthread.start();

		return INSTANCE;
	}

	public boolean pending(List<GetAds> list, String url) {
		for (GetAds x : list) {
			if (x.getUrl().equals(url))
				return true;
		}

		return GetAds.pending(url);
	}

	public void start() {
		ScheduledExecutorService expireService = Executors.newScheduledThreadPool(1);
		expireService.scheduleAtFixedRate(() -> {
			handleExpired();
		}, 0L, 24L, TimeUnit.HOURS);

	}

	void handleExpired() {

	}

	public Ads webAccess(int port) throws Exception {
		Runnable access;
		access = () -> {
			Server server = new Server(port);
			logger.info("**** Ads.txt App-Ads.txt ACCESS ON PORT: {}", port);
			AdsHandler handler = new AdsHandler(this);
			SessionHandler sh = new SessionHandler(); // org.eclipse.jetty.server.session.SessionHandler
			sh.setHandler(handler);

			server.setHandler(sh); // set session handle
			try {
				server.start();
				server.join();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Thread nthread = new Thread(access);
		nthread.start();
		return this;
	}

}

@MultipartConfig
class AdsHandler extends AbstractHandler {
	/**
	 * The property for temp files.
	 */

	protected static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	SessionHandler sh = new SessionHandler(); // org.eclipse.jetty.server.session.SessionHandler

	Ads ads;

	public AdsHandler(Ads parent) {
		this.ads = parent;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type");

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		InputStream body = request.getInputStream();
		String data = new String(body.readAllBytes());
		String[] lines = data.split("\n");
		for (int i = 0; i < lines.length; i++) {
			ads.offer(lines[i].trim());
		}
		response.setStatus(200);;
		response.getWriter().println("OK");
	}
}
