package com.game.src.main;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;

import com.game.src.main.classes.EntityA;
import com.game.src.main.classes.EntityB;

public class Game extends Canvas implements Runnable{
	
	private static final long  serialVersionUID = 1L;
	public static final int WIDTH = 320;   //value = 320 
	public static final int HEIGHT = WIDTH / 12 * 9; //aspect ratio.
	public static final int SCALE = 2;
	public final String TITLE = "BAXTER";
	
	
	Random r = new Random();  //my
	
	
	private Thread thread;
	private boolean running = false;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT,BufferedImage.TYPE_INT_RGB);
	private BufferedImage spriteSheet = null;
	private BufferedImage background = null;
	
	
	private boolean is_shooting = false;
	
	private int enemy_count = 10;  //==1
	private int enemy_killed = 0;
	
	private Player p;
	private Controller c;
	private Textures tex;
	
	public LinkedList<EntityA> ea;
	public LinkedList<EntityB> eb;
	
	public void init(){
		requestFocus();
		BufferedImageLoader loader = new BufferedImageLoader();
		try{
			
			spriteSheet = loader.loadImage("/sprite_sheet.png");
			background = loader.loadImage("/star_field_-_3.jpg");
		}catch(IOException e){
			e.printStackTrace();
		}
		
		tex = new Textures(this);  // before player and controller ..grab image
		
		p = new Player(200,200,tex);
		//origin all c = new Controller(this,tex);
		c = new Controller(tex);
		
		ea = c.getEntityA();
		eb = c.getEntityB();
		
		this.addKeyListener(new KeyInput(this));
		
		c.createEnemy(enemy_count);
	}
	
	private synchronized void start(){
		if(running)
			return;
		running = true;
		thread = new Thread(this);		//thread constructor
		thread.start();  // calls run() method
	}
	
	private synchronized void stop(){
		if(!running)
			return;
		running = false;
		try{
			thread.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		System.exit(1);
	}
	
	public void run(){		//interface Runnable
		init();
		long lastTime = System.nanoTime();
		final double  amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int updates = 0;
		int frames = 0;
		long timer = System.currentTimeMillis();
		
		while(running){
			//("Working");
			long now = System.nanoTime();
			delta+= ( now - lastTime)/ns;
			
			if(delta>=1){
				tick();  //everything game updates
				updates++;
				delta--; 		
			}
			render();  //everything game render
			frames++;
			if(System.currentTimeMillis() - timer > 1000 ){
				timer+=1000;
				System.out.println(amountOfTicks + "Ticks, fps"+ frames);  //updates ??
				updates = 0;
				frames = 0;
			}
			
		}
		stop();
	}
	
	private void tick(){
		p.tick();
		c.tick();
		try {
		    Thread.sleep(25);                 //delay :  1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
	
	
	private void render(){
		//Buffered Image loading
		//handle everything behind the scene
		BufferStrategy bs = this.getBufferStrategy(); // this takes Canvas Class
		
		if(bs == null){
			createBufferStrategy(3);  // 3 buffers before ahead time
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		///////////////////////////////////////
		
		g.drawImage(image,0,0,getWidth(),getHeight(),this);
		
		g.drawImage(background,0,0,null);
				
		p.render(g);
		c.render(g);
		//////////////////
		
		g.dispose();
		bs.show();
	}
	

	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_RIGHT){
			p.setVelX(5);
		}else if(key == KeyEvent.VK_LEFT){
			p.setVelX(-5);
		}else if(key == KeyEvent.VK_DOWN){
			p.setVelY(5);
		}else if(key == KeyEvent.VK_UP){
			p.setVelY(-5);
		}
		
		if(key == KeyEvent.VK_SPACE && !is_shooting ){
			c.addEntity(new Bullet(p.getX(),p.getY(), tex,this));
			is_shooting = true;
		
		}
	}
	
	public void keyReleased(KeyEvent e){
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_RIGHT){
			p.setVelX(0);
		}else if(key == KeyEvent.VK_LEFT){
			p.setVelX(0);
		}else if(key == KeyEvent.VK_DOWN){
			p.setVelY(0);
		}else if(key == KeyEvent.VK_UP){
			p.setVelY(0);
		}else if(key == KeyEvent.VK_SPACE){
			is_shooting = false;
		}
		
	}
	
	
	
	public static void main(String[] args){
	Game game = new Game();
	
	game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
	game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
	game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
	
	JFrame frame = new JFrame(game.TITLE);
	frame.add(game);
	frame.pack();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setResizable(false);
	frame.setLocationRelativeTo(null);
	frame.setVisible(true);
	
	game.start();
	}

	public BufferedImage getSpriteSheet(){
		return spriteSheet;
	}
	
	public int getEnemy_count(){
		return enemy_count;
	}
	
	public void setEnemy_count(int enemy_count){
		this.enemy_count = enemy_count;
	}
	
	public int getEnemy_killed(){
		return enemy_killed;
	}
	
	public void setEnemy_killed(int enemy_killed){
		this.enemy_killed = enemy_killed;
	}
	
	
}
