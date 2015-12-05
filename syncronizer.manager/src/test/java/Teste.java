import ag.ifpb.pod.rmi.core.DatastoreService;
import ag.ifpb.pod.rmi.core.TeacherTO;
import br.edu.ifpb.pod.syncronizer.manager.Loader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author douglasgabriel
 */
public class Teste {
    
    public Teste() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("200.129.71.228", 9090);        
        DatastoreService service = (DatastoreService) registry.lookup("DatastoreService");
        for (TeacherTO to : service.listTeachers()){
            to.setName(to.getName() + " Teste");
            service.createTeacher(to);
            break;
        }        
//        Loader.main(null);
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
     @Test
     public void hello() {
         
     }
}
