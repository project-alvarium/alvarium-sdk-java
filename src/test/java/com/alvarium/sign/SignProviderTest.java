import com.alvarium.sign.SignProvider;
import com.alvarium.sign.SignProviderFactory;
import com.alvarium.sign.SignType;
import com.alvarium.sign.SignTypeException;
import org.junit.Test;  
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

public class SignProviderTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void factoryShouldReturnNoConcreteTypesError() throws SignTypeException {
    thrown.expect(SignTypeException.class);
    final SignProviderFactory factory = new SignProviderFactory();
    final SignProvider signProvider = factory.getProvider(SignType.none);   
  }
}