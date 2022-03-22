package dams.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;

import server.AppointmentType;
import server.HospitalServerInterface;
import server.HospitalServerInterfaceHelper;
import dams.model.Configuration;

public class MontrealServer {
	private static MTLHospitalServer serverObj = null;
	private static String montrealNaming = "MTLServer";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			ORB orb = ORB.init(args, null);

			POA rootpoa = (POA) orb.resolve_initial_references("RootPOA");
			rootpoa.the_POAManager().activate();

			serverObj = new MTLHospitalServer();
			serverObj.setORB(orb);

			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(serverObj);

			HospitalServerInterface href = HospitalServerInterfaceHelper.narrow(ref);
			// get the root naming context
			// NameService invokes the transient name service

			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

			// Use NamingContextExt, which is part of the
			// Interoperable Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// bind the Object Reference in Naming
			NameComponent[] path = ncRef.to_name(montrealNaming);
			ncRef.rebind(path, href);
			System.out.println("Montreal server is running..");

			Runnable montrealServerRunnable = () -> {
				montrealUDPServer();
			};

			Thread montrealServerThread = new Thread(montrealServerRunnable);
			montrealServerThread.start();

			// wait for invocations from clients
			orb.run();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void montrealUDPServer() {
		// TODO Auto-generated method stub
		boolean running = true;
		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket(Configuration.MTL_LISTENER_PORT_NUMBER);

			while (running) {
				byte[] buffer = new byte[1024];

				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				InetAddress address = packet.getAddress();
				int port = packet.getPort();
				String received = new String(packet.getData(), 0, packet.getLength()).trim();
				System.out.println(received);
				String parameters = received.split(":")[1];
				String response = "";
				if (received.startsWith("listAppointment")) {
					AppointmentType appointmentType = Configuration.MAP_OF_APPOINTMENT_TYPE.get(parameters);
					response = serverObj.montrealListAppointmenOfType(appointmentType);
				} else if (received.startsWith("getSchedule")) {
					response = serverObj.montrealAppointmentScheduleOfPatientId(parameters);
				} else if (received.startsWith("bookAppointment")) {
					String[] otherParameters = parameters.split(",");
					AppointmentType appointmentType = Configuration.MAP_OF_APPOINTMENT_TYPE.get(otherParameters[2]);

					response = serverObj.montrealBookAppointment(otherParameters[0], otherParameters[1],
							appointmentType);
				} else if (received.startsWith("cancelSchedule")) {
					String[] otherParameters = parameters.split(",");
					response = serverObj.montrealCancelAppointmentOfPatientId(otherParameters[0], otherParameters[1]);
				}

				buffer = response.getBytes();
				packet = new DatagramPacket(buffer, buffer.length, address, port);
				socket.send(packet);
			}
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
