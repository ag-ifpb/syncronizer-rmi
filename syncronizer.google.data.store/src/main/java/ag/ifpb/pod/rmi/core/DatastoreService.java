package ag.ifpb.pod.rmi.core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DatastoreService extends Remote {
  void createTeacher(TeacherTO to) throws RemoteException;
  void updateTeacher(TeacherTO to) throws RemoteException;
  List<TeacherTO> listTeachers() throws RemoteException;
}
