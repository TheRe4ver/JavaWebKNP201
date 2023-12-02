package step.learning.services.random;

import java.util.Random;

public class RandomSrviceV1 implements RandomService {
    private String iv;
    private final Random random;
    public RandomSrviceV1() {
        this.random = new Random();
    }
    @Override
    public void seed(String iv) {
        this.iv = iv;
        random.setSeed( iv == null ? 0 : Long.parseLong( iv ) ) ;
    }
    @Override
    public String randomHex(int charLength) {
        char[] hexChars = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        if(charLength < 1) {
            throw new RuntimeException("charLength should be positive");
        }
        StringBuilder sb = new StringBuilder() ;
        for (int i = 0; i < charLength; i++) {
            sb.append( hexChars[ random.nextInt(hexChars.length ) ] ) ;
        }
        return sb.toString();
    }
}
