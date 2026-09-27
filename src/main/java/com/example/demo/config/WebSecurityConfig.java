package com.example.demo.config;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = "com")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private UserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// configure AuthenticationManager so that it knows from where to load
		// user for matching credentials
		// Use BCryptPasswordEncoder
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public JwtRequestFilter authenticationTokenFilterBean() throws Exception {
		return new JwtRequestFilter();
	}

	@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(java.util.Arrays.asList("http://localhost:4200"));
    configuration.setAllowedMethods(java.util.Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
    ));
    configuration.setAllowedHeaders(java.util.Arrays.asList("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		// We don't need CSRF for this example

		httpSecurity.cors().and().csrf().disable()
				// dont authenticate this particular request
				.authorizeRequests()
                                .antMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
				.antMatchers( "/api/academy-parade-state/get-parade-state-list","/hello", "/admin/login", "/user/login",
						"/cadet/login", "/edossier/login", "/staff/login",
						"/user/add-user", "/admin/addAdmin", "/api/dailyPrgmController/getTodaysPrograme",
						"/api/eventsController/upcomingEvents", "/api/messageCommandantController/getMessagesByStatus",
						"/api/messageCommandantController/getLatestMsgByStatus",
						"/api/imaActivityController/getActivityByStatus", "/api/greyBookController/getGreyBookRecords",
						"/api/centralLibraryController/getAllCentralLibraryRecordHomePage",
						"/api/cyberPolicyTypeController/getAllPolicyTypeHomePage",
						"/api/podcastController/getAllPodCastList", "/api/greyBookController/getGreyBookRecords",
						"/adjutant/getByAdjutantBranch", "/api/forecast/get-forecast-list", "/",
						"/api/syllabus/term/get-syllabus-term-list", "/api/syllabus/get-syllabus-list",
						"/api/performance-highlights/get-performance-highlights-list",
						"/api/gc-activities/get-gc-activities-list", "/api/battalion/get-battalion-list",
						"/api/week/get-week-list", "/api/term/getAllTerms", "/termSeason/getSeasonTerm",
						"/api/assaigment_of_duties/getAll", "/api/special-occasion/get-week-occasions",
						"/api/commandant-recommended-reading-list/get-recommended-book-list",
						"/api/trg-calendar/get-weekly-program-week", "/api/trg-calendar/get-weekly-schedule-date",
						"/api/OrganizationChartController/active-positions", "/api/gsController/get-gsOrg-list",
						"/api/battalion/get-organization-list", "/api/trg-calendar/get-current-week-schedule",
						"/v2/api-docs", // swagger
						"/api/trg-calendar/get-weekly-program-week", "/api/trg-calendar/get-weekly-schedule-date",
						"/api/trg-calendar/get-current-week-schedule", "/api/hall-of-fame/get-officers-fame-list",
						"/api/hall-of-fame/get-param-veer-fame-list", "/api/blog/get-blog-list-by-category",
						"/api/announcement/get-announcement-list", "/api/section-hospital/get-list",
						"/api/gc_board/get-list", "/api/special-occasion/get-all-occasion", "/api/blog/get-blog-list",
						"/api/blog/get-blog", "/api/eventsController/getEventsByDates", "/v2/api-docs", // swagger
						"/webjars/**", // swagger-ui webjars
						"/swagger-resources/**", // swagger-ui resources
						"/configuration/**", // swagger configuration
						"/*.html", "/favicon.ico", "/**/*.html", "/**/*.css", "/**/*.js",
						"/api/exercisesController/getAllExerciseTypeList",
						"/api/entryTypeController/getAllEntryTypeList",
						"/api/gc_board/get-gcBoard_Pcht_Ol_AchievementsList", "/AllUser/login",
						"/api/battalion/get-company-by-battalion", "/api/trg-calendar/get-week-schedule-by-date",
						"/api/acadmic_syllabus_controller/getall_academic_syllabus_by_status",
						"/api/acadmic_syllabus_controller/get_academic_syllabus_List_by_termid",
						"/api/studymaterial/get-studymaterial-list","/api/academic_files/add-adcademicFiles-new","/api/home_controller/*",
						"/api/homeEbookController/*")// 2   //ebook controller Akash 18-08-2023
				// may
				// added
				.permitAll().
				// all other requests need to be authenticated
				anyRequest().authenticated().and().
				// make sure we use stateless session; session won't be used to
				// store user's state.
				exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// Add a filter to validate the tokens with every request
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

	}

}
