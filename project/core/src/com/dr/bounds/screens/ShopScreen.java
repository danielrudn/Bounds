package com.dr.bounds.screens;

import java.util.ArrayList;

import com.DR.dLib.ui.dScreen;
import com.DR.dLib.ui.dUICard;
import com.DR.dLib.ui.dUICardList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.dr.bounds.ui.ShopItemCard;

public class ShopScreen extends dUICardList implements HttpResponseListener{

	// Holds 2 ShopItemCards 
	private static ArrayList<dUICard> itemCardContainers = new ArrayList<dUICard>();
	private Texture cardTexture;
	// String for shop file
	private final String url ="https://docs.google.com/document/d/1fapoD_xnTPEAYMpJUGr9zWHy1vKzDvybvoDcWkcHQOU/edit?usp=sharing";
	private final String LESS_THAN = "\\u003c", GREATER_THAN = "\\u003e";
	private final String[] symbols = new String[]{"\\u003c", "\\u003e", "\\u003d", "’","\\n", "\\t"};
	private final String[] actual = new String[]{"<", ">","=","'","\n","\t"};
	private dUICard currentContainer;
	private dUICard itemCard;
	private HttpRequest request;
	private String response = "";
	
	public ShopScreen(float x, float y, Texture texture) {
		super(x, y, texture, itemCardContainers);
		cardTexture = texture;
		loadItemsFromBackend();
		//this.setTitleCard(null);
	}
	
	private void loadItemsFromBackend()
	{
		request = new HttpRequest(HttpMethods.GET);
		request.setUrl(url);
		request.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:12.0) Gecko/20100101 Firefox/21.0");
		Gdx.net.sendHttpRequest(request, this);

		for(int x = 0; x < 20; x++)
		{

		}
	}

	@Override
	public void goBack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switchScreen(dScreen arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleHttpResponse(HttpResponse httpResponse) {
		response = httpResponse.getResultAsString();
		response = response.substring(response.indexOf(LESS_THAN + "Shop" + GREATER_THAN), response.indexOf(LESS_THAN + "/Shop" + GREATER_THAN) + (LESS_THAN + "/Shop" + GREATER_THAN).length());
		for(int x = 0; x < symbols.length; x++)
		{
			response = response.replace(symbols[x], actual[x]);
		}
		loadDataFromXML(response);
		Gdx.net.cancelHttpRequest(request);
	}
	
	private void loadDataFromXML(String xmlString)
	{
		XmlReader reader = new XmlReader();
		
		Element shop = reader.parse(response);
		for(int x = 0; x < shop.getChildrenByName("Item").size; x++)
		{
			final Element e = shop.getChildrenByName("Item").get(x);
			final int currentIndex = x;
		//	System.out.println("Item - Name: " + e.get("name") + " ID: " + e.get("id") + " price: " + e.get("price"));
			Gdx.app.postRunnable(new Runnable()
			{
				@Override
				public void run() {
					itemCard = new ShopItemCard(0,0,cardTexture,e.get("name"), Integer.parseInt(e.get("price")), Byte.parseByte(e.get("id")));
					if(currentIndex%2 == 0)
					{
						currentContainer = new dUICard(0,0,cardTexture);
						currentContainer.setDimensions(getWidth() - 256f, 222f);
						currentContainer.setHasShadow(false);
						currentContainer.setAlpha(0);
						//currentContainer.setColor(Color.RED);
					//	itemCardContainers.add(currentContainer);
						currentContainer.addObject(itemCard, dUICard.LEFT, dUICard.CENTER);
					}
					else
					{
						currentContainer.addObject(itemCard, dUICard.RIGHT, dUICard.CENTER);
						addCardAsObject(currentContainer);
					}	
					
				}
			});
		}
	}
	
	private void loadDataFromXML(FileHandle file)
	{
		
	}

	@Override
	public void failed(Throwable t) {

	}

	@Override
	public void cancelled() {

	}

}
