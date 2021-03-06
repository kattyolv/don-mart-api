package com.kattyolv.don.mart.api.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.kattyolv.don.mart.api.dao.DAOClient;
import com.kattyolv.don.mart.api.model.Client;


@WebServlet("/client")
public class ClientController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			
			DAOClient clientDAO = new DAOClient();
			ArrayList<Client> clients = clientDAO.selectClient();
			
			Gson gson = new Gson();
			String clientsJson = gson.toJson(clients);
			
			PrintWriter out = response.getWriter();
			out.print(clientsJson);
			
			response.setContentType("application/json");
			response.setStatus(200);
			
		}
		catch(Exception e) {
			e.printStackTrace();
			response.setStatus(500);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			
			DAOClient clientDAO = new DAOClient();
			
			String name = request.getParameter("name");
			String address = request.getParameter("address");
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			
			if(name != "" &&
					address != "" &&
					email != "" &&
					password != "") {
				
				Client client = new Client();
				
				client.setName(name);
				client.setAddress(address);
				client.setEmail(email);
				client.setPassword(password);
				
				boolean wasInserted = clientDAO.insertClient(client);
				
				if(wasInserted == true) {
					response.setStatus(200);
				}
			}
			else {
				response.setStatus(400);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			response.setStatus(500);
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			
			DAOClient clientDAO = new DAOClient();
			
			Client client = new Client();
			
			InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			
			String bodyRequest = bufferedReader.readLine();
			
			String[] bodyRequestSplitted = bodyRequest.split("&");
			
			for(String infoBodyRequest : bodyRequestSplitted) {
				
				String[] infoBodyRequestSplitted = infoBodyRequest.split("=");
				
				if(infoBodyRequestSplitted.length == 1) {
					response.setStatus(400);
					return;
				}
				
				String key = infoBodyRequestSplitted[0];
				String value = infoBodyRequestSplitted[1];
				
				String decodedValue = URLDecoder.decode(value, "UTF-8");
				
				switch(key) {
					case "name":
						client.setName(decodedValue);
						break;
					case "address":
						client.setAddress(decodedValue);
						break;
					case "email":
						client.setEmail(decodedValue);
						break;
					case "password":
						client.setPassword(decodedValue);
						break;
					default:
						response.getWriter().print("Invalid key.");
						break;
				}
			}
			
			boolean wasUpdated = clientDAO.updateClient(client);
			
			if(wasUpdated == true) {
				response.setStatus(200);
			}
			else {
				response.setStatus(400);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			response.setStatus(500);
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			
			DAOClient clientDAO = new DAOClient();
				
			InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			
			String bodyRequest = bufferedReader.readLine();
			
			if(bodyRequest != null) {
				
				String[] bodyRequestSplitted = bodyRequest.split("=");
				
				String keyRequestId = bodyRequestSplitted[0];
				
				if(bodyRequestSplitted.length == 1 ||
						!keyRequestId.equalsIgnoreCase("id")) {
					
					response.getWriter().print("id is required.");
					response.setStatus(400);
					return;
				}
				
				String idValue = bodyRequestSplitted[1];
				
				int convertedId = Integer.parseInt(idValue);
				
				boolean hasDeleted = clientDAO.deleteClient(convertedId);
				
				if(hasDeleted == true) {
					response.setStatus(200);
					return;
				}
			}
			
			response.setStatus(400);
			
		}
		catch(Exception e) {
			e.printStackTrace();
			response.setStatus(500);
		}
	}

}
