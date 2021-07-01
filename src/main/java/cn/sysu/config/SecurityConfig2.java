package cn.sysu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig2 extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //退出登录
        http.logout()
                //退出登录路径
                .logoutUrl("/logout")
                //退出登录成功后跳转路径
                .logoutSuccessUrl("/test/log_success")
                //允许任意权限的人操作
                .permitAll();

        //自定义无权限跳转的页面，即403
        http.exceptionHandling().accessDeniedPage("/unauth.html");

        //自定义登录页面
        http.formLogin()
                //登录页面设置
                .loginPage("/login.html")
                //登录访问路径
                .loginProcessingUrl("/login")
                //登录成功后跳转路径
                .defaultSuccessUrl("/index").permitAll()
                .and().authorizeRequests()
                //设置哪些路径可以通过
                .antMatchers("/login","/test/hello").permitAll()
                //设置拥有哪些权限的人可以访问
                .antMatchers("/admin/index").hasAuthority("admin")
                //设置拥有哪些权限的人可以访问(多种权限类型)
                .antMatchers("/admin/index","admin/index2").hasAnyAuthority("admin,superuser")
                .anyRequest().authenticated()
                //关闭csrf防护
                .and().csrf().disable();
    }
}
