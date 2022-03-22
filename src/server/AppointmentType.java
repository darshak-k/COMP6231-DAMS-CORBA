package server;

/**
 * server/AppointmentType.java . Generated by the IDL-to-Java compiler
 * (portable), version "3.2" from HospitalServer.idl Sunday, 20 February, 2022
 * 11:09:53 PM EST
 */

public class AppointmentType implements org.omg.CORBA.portable.IDLEntity {
	private int __value;
	private static int __size = 3;
	private static server.AppointmentType[] __array = new server.AppointmentType[__size];

	public static final int _PHYSICIAN = 0;
	public static final server.AppointmentType PHYSICIAN = new server.AppointmentType(_PHYSICIAN);
	public static final int _SURGEON = 1;
	public static final server.AppointmentType SURGEON = new server.AppointmentType(_SURGEON);
	public static final int _DENTAL = 2;
	public static final server.AppointmentType DENTAL = new server.AppointmentType(_DENTAL);

	public int value() {
		return __value;
	}

	public static server.AppointmentType from_int(int value) {
		if (value >= 0 && value < __size)
			return __array[value];
		else
			throw new org.omg.CORBA.BAD_PARAM();
	}

	protected AppointmentType(int value) {
		__value = value;
		__array[__value] = this;
	}

	@Override
	public String toString() {
		if (value() == 0) {
			return "PHYSICIAN";
		}
		if (value() == 1) {
			return "SURGEON";
		}
		if (value() == 2) {
			return "DENTAL";
		}
		return "";
	}
} // class AppointmentType