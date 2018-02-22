package cn.luvletter.util;

import cn.luvletter.bean.AuthenticationBean;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zephyr Ji
 * @ Description: TODO
 * @ Date 2018/2/13
 */
public class JWTUtil {
   private static final String TOKEN_PREFIX = "Bearer";
   private static final String HEADER_STRING = "Authorization";

    /**
     * @Description:    添加head
     * @Date: 17:37 2018/2/13
     */
    public static String addAuthentication(HttpServletResponse response, AuthenticationBean authenticationBean) throws UnsupportedEncodingException {
       //签发时间
       Date iatDate = new Date();
       //过期时间 1分钟过期
       Calendar nowTime = Calendar.getInstance();
       nowTime.add(Calendar.MINUTE,1);
       Date expiresDate = nowTime.getTime();

       Map<String,Object> map = new HashMap<>();
       map.put("alg","HS256");
       map.put("typ","JWT");
       String token = JWT.create()
               .withHeader(map)//header
               .withClaim("account",authenticationBean.getAccount())//payload
               .withClaim("role",authenticationBean.getRoleNo())
               .withExpiresAt(expiresDate)//过期时间，大于签发时间
               .withIssuedAt(iatDate)//签发时间
               .sign(Algorithm.HMAC256(authenticationBean.getPassword()));
        response.addHeader(HEADER_STRING,TOKEN_PREFIX+" "+token);
        return token;
   }

   public static String getAuthentication(HttpServletRequest request) {
       return request.getHeader(HEADER_STRING).replaceAll(TOKEN_PREFIX,"").trim();
   }

   public static boolean validateToken(String token,String secret) throws UnsupportedEncodingException {
       if(token != null){
           JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();
           DecodedJWT jwt = null;
           try{

               jwt = jwtVerifier.verify(token);
           }catch (Exception e){
               System.out.println("凭证已过期");
           }
           return true;
       }
       return false;
   }

    public static String getUsernameFromToken(String authHead) {
            return JWT.decode(authHead).getClaim("account").asString();
    }
}
