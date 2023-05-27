package com.dices.game;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Dices extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Socket socket;
	Skin skin;
	Stage stage;
	TextArea chat;
	TextField mensage;
	TextButton send;
	
	public void setgui() {
		
		Table wrapper = new Table();
		
		wrapper.setFillParent(true);
		
		stage.addActor(wrapper);
		
		chat = new TextArea(null, skin);
		mensage = new TextField(null, skin);
		send = new TextButton(null, skin);
		
		chat.pack();
		chat.setDisabled(true);
		
		send.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				JSONObject data = new JSONObject();
				try {
					data.put("msg", mensage.getText());
					socket.emit("msg", data);				
					chat.appendText("you :\n" + mensage.getText() + "\n");
					mensage.setText("");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
	    });
				
		wrapper.add(chat).colspan(2).grow();
		wrapper.row();
		wrapper.add(mensage).growX();
		wrapper.add(send);
		
		Gdx.input.setInputProcessor(stage);
		
	}
	
	@Override
	public void create () {
		skin = new Skin(Gdx.files.internal("gui.json"));
		stage = new Stage(new ScreenViewport());
		
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		setgui();
		connectSocket();
		configSocketEvents();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
		
		batch.begin();
		
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		stage.dispose();
	}
	
	public void connectSocket() {
		try {
			socket = IO.socket("http://localhost:8080");
			socket.connect();
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	public void configSocketEvents() {
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			
			@Override
			public void call(Object... args) {
				System.out.println("connected\n");
			}
		}).on("info", new Emitter.Listener() {
			
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				chat.appendText("welcome\n");
				try {
					chat.appendText("client id : " + data.getString("id") + "\n");					
				} catch (JSONException e) {
					e.printStackTrace();
				}				
			}
		}).on("playerConnected", new Emitter.Listener() {
			
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					chat.appendText(data.getString("player") + " connected\n" );					
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
		}).on("playerDiconnected", new Emitter.Listener() {
			
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					chat.appendText(data.getString("player") + " disconnected\n" );					
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
		}).on("newMessage", new Emitter.Listener() {
			
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					chat.appendText(data.getString("sender") + " :\n" + data.getString("msg") + "\n" );					
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
		});
		
	}
}
