package org.nerdronix.springnetty.cfg;
import java.net.InetSocketAddress;

import org.nerdronix.springnetty.websocketx.server.WebSocketServerInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.dissidia986.service.AppService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


/**
 * This class contains the bean definitions for this netty server. These beans
 * are autowired into the respective java classes in org.nerdronix.* packages
 * using component scan feature of spring. Properties are injected using the
 * PropertySource. For more information look <a href=
 * "http://static.springsource.org/spring/docs/3.2.2.RELEASE/javadoc-api/org/springframework/context/annotation/Configuration.html"
 * >here</a> and <a href=
 * "http://blog.springsource.com/2011/06/10/spring-3-1-m2-configuration-enhancements/"
 * >here</a>
 * 
 * @author Abraham Menacherry
 * 
 */
@Configuration
@ComponentScan("com.dissidia986.*, org.nerdronix.*")
@PropertySource("classpath:netty-server.properties")
public class SpringConfig {

	@Value("${boss.thread.count}")
	private int bossCount;

	@Value("${worker.thread.count}")
	private int workerCount;

	@Value("${tcp.port}")
	private int tcpPort;

	@Value("${so.keepalive}")
	private boolean keepAlive;

	@Value("${so.backlog}")
	private int backlog;
	
	@Value("${log4j.configuration}")
	private String log4jConfiguration;

	@Autowired
	private AppService appService;
	
	@SuppressWarnings("unchecked")
	@Bean(name = "serverBootstrap")
	public ServerBootstrap bootstrap() {
		if(appService==null){
			System.err.println("==========");
		}else{
			System.err.println("----------");
		}
		ServerBootstrap b = new ServerBootstrap();
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
		b.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new WebSocketServerInitializer(appService));
		
		
		b.option(ChannelOption.SO_KEEPALIVE, keepAlive);
		b.option(ChannelOption.SO_BACKLOG, backlog);
		return b;
	}

	@Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup bossGroup() {
		return new NioEventLoopGroup(bossCount);
	}

	@Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup workerGroup() {
		return new NioEventLoopGroup(workerCount);
	}

	@Bean(name = "tcpSocketAddress")
	public InetSocketAddress tcpPort() {
		return new InetSocketAddress(tcpPort);
	}

	@Bean(name = "stringEncoder")
	public StringEncoder stringEncoder() {
		return new StringEncoder();
	}

	@Bean(name = "stringDecoder")
	public StringDecoder stringDecoder() {
		return new StringDecoder();
	}

	/**
	 * Necessary to make the Value annotations work.
	 * 
	 * @return
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
}
