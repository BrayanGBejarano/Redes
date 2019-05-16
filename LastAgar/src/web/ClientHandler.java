package web;
import java.net.*;
import java.util.StringTokenizer;
import java.io.*;
public class ClientHandler implements Runnable{
	
	private final Socket socket;
	
	public boolean existe = false;

	public ClientHandler(Socket socket)
	{
		this.socket =  socket;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("\nClientHandler Started for " + this.socket);
		while(true) 
		{
			handleRequest(this.socket);
		}		
		//System.out.println("ClientHandler Terminated for "+  this.socket + "\n");
	}
	
	public void handleRequest(Socket socket)
	{
		try {
			
			
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String headerLine = in.readLine();
			if(headerLine!=null)
			{
				
			
				System.out.println(headerLine);
				// A tokenizer is a process that splits text into a series of tokens
				StringTokenizer tokenizer =  new StringTokenizer(headerLine);
				//The nextToken method will return the next available token
				String httpMethod = tokenizer.nextToken();
				// The next code sequence handles the GET method. A message is displayed on the
				// server side to indicate that a GET method is being processed
				if(httpMethod.equals("GET"))
				{
					System.out.println("Get method processed");
					String httpQueryString = tokenizer.nextToken();
					System.out.println(httpQueryString);
					if(httpQueryString.equals("/"))
					{
						StringBuilder responseBuffer =  new StringBuilder();
						String str="";
						BufferedReader buf = new BufferedReader(new FileReader(System.getProperty("user.dir") +"/src/web/templates/javascr.html"));
						while ((str = buf.readLine()) != null) {
							responseBuffer.append(str);
					    }
				
						sendResponse(socket, 200, responseBuffer.toString());		
					    buf.close();
					}
					if(httpQueryString.contains("/?login="))
					{
						
					//	StringBuilder puntajeBuffer =  new StringBuilder();		
						

						
						System.out.println("Get method processed");
						String[] response =  httpQueryString.split("login=");
						
						String login="";
						
						BufferedReader log = new BufferedReader(new FileReader("./data/Base.txt"));
						while ((login = log.readLine()) != null && !existe) {
							if(response[1].equals(login)) {
								existe = true;
							}
						}
						
						if(existe == true) {
							
							
							
							String[] puntajeBuffer =  new String[500];
							String pun="";
							BufferedReader puntaje = new BufferedReader(new FileReader("./data/"+response[1]+".txt"));
							int i = 0;
							while ((pun = puntaje.readLine()) != null) {
								puntajeBuffer[i] = pun.split(" ")[0];
								puntajeBuffer[i+1] = pun.split(" ")[1];
								puntajeBuffer[i+2] = pun.split(" ")[2];
								puntajeBuffer[i+3] = pun.split(" ")[3];
								puntajeBuffer[i+4] = pun.split(" ")[4];
								i+=5;
						    }
							int juegos = i/5;
		
						    puntaje.close();
							
							
						    StringBuilder responseBuffer =  new StringBuilder();
							responseBuffer
							.append("<html>")
							.append("<head> <title> Agar.IO! </title> "
//									+ "<script> "
//									+ "window.setTimeout(brayan, 10);"
//									+ "function brayan(){"
//									+ "alert(\"Ha ingresado satisfactoriamente!!\");"
//									+ "};"
//									+ "</script> "
									+ "<style>"
//									+ "body{"								
//									+ "cursor: url(\"http://www.banderas-del-mundo.com/America_del_Sur/Colombia/colombia_mwd.gif\"), auto;"
//									+ "}"
									+ "</style>"
									+ "</head> ")
							.append("<body background ='https://vignette.wikia.nocookie.net/pewdiepie/images/3/3f/Agar.jpg/revision/latest?cb=20190223193900'>")
							.append("<h1 style='color:black'> Usuario: " + response[1] + " ha jugado: " + juegos +" partidas"+ "</h1>")
							.append("<h1>PUNTAJES</h1>")
							.append("<table>")
							.append("<tr>\r\n" + 
									"				<td>Fecha</td>\r\n" + 
									"				<td>Numero de Alimentos</td>\r\n" + 
									"				<td>Score</td>\r\n" +
									"				<td>Gano?</td>\r\n" +
									"				<td>Tiempo de juego</td>\r\n" +
									"\r\n" + 
									"</tr>");
							int lineas = 0;
							int actual = 0;
							while (lineas < juegos) {
								responseBuffer
								.append("<tr>\r\n" + 
										"				<td>" + puntajeBuffer[actual]+ "</td>\r\n" + 
										"				<td>" + puntajeBuffer[actual+1]+ "</td>\r\n" +
										"				<td>" + puntajeBuffer[actual+2]+ "</td>\r\n" +
										"				<td>" + puntajeBuffer[actual+3]+ "</td>\r\n" +
										"				<td>" + puntajeBuffer[actual+4]+ "</td>\r\n" +
										"\r\n" + 
										"</tr>");
								lineas++;
								actual+=5;
							}
							responseBuffer
//							.append("<tr>\r\n" + 
//									"				<td>" + puntajeBuffer[0]+ "</td>\r\n" + 
//									"				<td>" + puntajeBuffer[1]+ "</td>\r\n" +
//									"				<td>" + puntajeBuffer[2]+ "</td>\r\n" +
//									"				<td>" + puntajeBuffer[3]+ "</td>\r\n" +
//									"				<td>" + puntajeBuffer[4]+ "</td>\r\n" +
//									"\r\n" + 
//									"</tr>")
//							.append("<tr>\r\n" + 
//									"				<td>" + puntajeBuffer[2]+ "</td>\r\n" + 
//									"				<td>" + puntajeBuffer[3]+ "</td>\r\n" +
//									"				<td>" + puntajeBuffer[1]+ "</td>\r\n" +
//									"				<td>" + puntajeBuffer[1]+ "</td>\r\n" +
//									"				<td>" + puntajeBuffer[1]+ "</td>\r\n" +
//									"\r\n" + 
//									"</tr>")
//							.append("<tr>\r\n" + 
//									"				<td>" + puntajeBuffer[4]+ "</td>\r\n" + 
//									"				<td>" + puntajeBuffer[5]+ "</td>\r\n" +
//									"				<td>" + puntajeBuffer[1]+ "</td>\r\n" +
//									"				<td>" + puntajeBuffer[1]+ "</td>\r\n" +
//									"				<td>" + puntajeBuffer[1]+ "</td>\r\n" +
//									"\r\n" + 
//									"</tr>")
//							.append("<img src='https://vignette.wikia.nocookie.net/pewdiepie/images/3/3f/Agar.jpg/revision/latest?cb=20190223193900'>")
//							.append("<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/mK4t8U3eSAI?autoplay=1\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" ></iframe>")
							.append("</body>")
							.append("</html>");
							sendResponse(socket, 200, responseBuffer.toString());		
						    
						}else {
							StringBuilder responseBuffer =  new StringBuilder();
							responseBuffer
							.append("<html>")
							.append("<head> <title> Agar.IO! </title> "
									+ "<style>"
									+ "</style>"
									+ "</head> ")
							.append("<body background ='https://vignette.wikia.nocookie.net/pewdiepie/images/3/3f/Agar.jpg/revision/latest?cb=20190223193900'>")
							.append("<h1 style='color:black'> Usuario: " + response[1] + " no se encuentra registrado" + "</h1>")
							.append("</body>")
							.append("</html>");
							sendResponse(socket, 200, responseBuffer.toString());		
						}
						
						
					}
										    
				}
				
				else
				{
					System.out.println("The HTTP method is not recognized");
					sendResponse(socket, 405, "Method Not Allowed");
				}
			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void sendResponse(Socket socket, int statusCode, String responseString)
	{
		String statusLine;
		String serverHeader = "Server: WebServer\r\n";
		String contentTypeHeader = "Content-Type: text/html\r\n";
		
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			if (statusCode == 200) {
				statusLine = "HTTP/1.0 200 OK" + "\r\n";
				String contentLengthHeader = "Content-Length: " + responseString.length() + "\r\n";
				out.writeBytes(statusLine);
				out.writeBytes(serverHeader);
				out.writeBytes(contentTypeHeader);
				out.writeBytes(contentLengthHeader);
				out.writeBytes("\r\n");
				out.writeBytes(responseString);
			} else if (statusCode == 405) {
				statusLine = "HTTP/1.0 405 Method Not Allowed" + "\r\n";
				out.writeBytes(statusLine);
				out.writeBytes("\r\n");
			} else {
				statusLine = "HTTP/1.0 404 Not Found" + "\r\n";
				out.writeBytes(statusLine);
				out.writeBytes("\r\n");
			}
			// out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}