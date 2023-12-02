package step.learning.services.random;

public interface RandomService {
    void seed ( String iv );
    String randomHex( int charLength );
}
