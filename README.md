모든 코드는 [github](https://github.com/WooWan/spring-csrf)에 있습니다.
## 들어가며
최근에 Spring security를 한창 공부하고 있는데 (조만간 블로그에 security 관련 글이 여러개 올라갈것 같다) 한 가지 궁금하게 한 코드가 있었다.

![](https://images.velog.io/images/woohobi/post/eebc1e7f-8aa7-4c43-853f-65d79aa8d017/image.png)

> http.csrf().disable()에서 csrf은 무엇이고, disable() 하는 이유가 무엇일까?

`csrf는 무엇일까?`
### CSRF
Cross site Request forgery로 사이즈간 위조 요청인데, 즉 정상적인 사용자가 의도치 않은 위조요청을 보내는 것을 의미한다.

예를 들어 A라는 도메인에서, 인증된 사용자 H가 위조된 request를 포함한 link, email을 사용하였을 경우(클릭, 또는 사이트 방문만으로도), A 도메인에서는 이 사용자가 일반 유저인지, 악용된 공격인지 구분할 수가 없다.



CSRF protection은 spring security에서 default로 설정된다. 즉, protection을 통해 GET요청을 제외한 상태를 변화시킬 수 있는 POST, PUT, DELETE 요청으로부터 보호한다. 

csrf protection을 적용하였을 때, html에서 다음과 같은 csrf 토큰이 포함되어야 요청을 받아들이게 됨으로써, 위조 요청을 방지하게 됩니다.
```
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
```

### Rest api에서의 CSRF
그래서 이렇게 보안 수준을 향상시키는 CSRF를 왜 disable 하였을까? spring security documentation에 non-browser clients 만을 위한 서비스라면 csrf를 disable 하여도 좋다고 한다.

![](https://images.velog.io/images/woohobi/post/bb399e9a-7f87-4f00-8b14-3f29bb239b6e/image.png)


이 이유는 rest api를 이용한 서버라면, session 기반 인증과는 다르게 stateless하기 때문에 서버에 인증정보를 보관하지 않는다. rest api에서 client는 권한이 필요한 요청을 하기 위해서는 요청에 필요한 인증 정보를(OAuth2, jwt토큰 등)을 포함시켜야 한다. 따라서 서버에 인증정보를 저장하지 않기 때문에 굳이 불필요한 csrf 코드들을 작성할 필요가 없다.

### 예시

송금을 담당하는 아래와 같은 transfer 함수가 있다고 할 때, H 사용자가 A 사이트에서 쿠키,세션 등으로 인증된 경우 위조된 요청이 들어왔을 때, 해당 요청이 실행된다.
``` java
    @PostMapping("/transfer")
    public void transfer2(@RequestParam int accountNo, @RequestParam final int amount) {
        log.warn("accountNo: {} , amount:{}", accountNo, amount);
        ...
    }

```
또, 아래와 같은 위조된 요청을 보낼 수 있는 코드를 가진 사이트에 일반 유저가 들어가게 된다면 img 태크에 포함된 url 요청으로 인해 위조된 GET 요청을 서버로 보낼 것이고, submit 버튼을 누른다면 서버로 위조된 post 요청을 보내게 될 것이다.
``` html
   //fakeBank.html
    <img src="http://localhost:8080/transfer?accountNo=5678&amount=1000"/>

    <form action="http://localhost:8080/transfer" method="POST">
        <input name="accountNo" type="hidden" value="5678"/>
        <input name="amount" type="hidden" value="1000"/>
        <input type="submit" value="Show Kittens Picture">
    </form>

```

http//localhost:8080/fake 에서 버튼을 누른다면 서버에 post요청을 보내게 되는데,

<img src="https://images.velog.io/images/woohobi/post/185f0010-eb52-46db-a64f-12bd5c2890a8/image.png" align="left"/>

---

위조된 post요청이 서버에서 이를 구별하지 못하고 post요청을 그대로 실행하게 된다.

![](https://images.velog.io/images/woohobi/post/b73a8688-0fcb-4d4b-8c44-463666770a7d/image.png)

따라서 이를 방지하기 위해, spring security에서는 기본적으로 csrf protection을 제공한다.

> Gradle에 spring-boot-starter security를 추가하고, 아래와 같은 SecurityConfig를 클래스를 만들어준다면, csrf 공격으로부터 방지한다. 

``` java
implementation 'org.springframework.boot:spring-boot-starter-security'
```

``` java
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {}

```
http//localhost:8080/fake 에서 버튼을 다시 누른다면 403으로 forbidden 요청을 반환한다.
![](https://images.velog.io/images/woohobi/post/3002a12a-c040-4229-915e-def93f638c2e/image.png)

하지만, 우리가 만들고자 하는 rest api에서는 csrf 공격으로부터 안전하고 매번 api 요청으로부터 csrf 토큰을 받지 않아도 되어 이 기능을 disable() 하는 것이 더 좋은 판단으로 보인다.

``` java
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
    }
}
```
