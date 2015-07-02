package com.game.src.main;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

import com.game.src.main.classes.EntityB;

public class Enemy extends GameObject implements EntityB{
	
	private Textures tex;
	
	Random r = new Random();
	private double speed = r.nextInt(5) + 1;
	
	public Enemy(double x,double y,Textures tex){
		super(x,y);
		this.tex = tex;
	}
	
	public void tick(){
		y+= speed;
		if( y > (Game.HEIGHT *Game.SCALE)){
			y =0; 
			x = r.nextInt(Game.WIDTH*Game.SCALE);
		}
	}
			
	public void render(Graphics g){
		g.drawImage(tex.enemy,(int)x,(int)y,null);
	}
	
	public Rectangle getBounds(){
		return new Rectangle((int)x,(int) y,32,32);
	}
	
	public double getY(){
		return y;
	}
	
	public double getX(){
		return x;
	}
	public void setY(double y){
		this.y = y;
	}
}
