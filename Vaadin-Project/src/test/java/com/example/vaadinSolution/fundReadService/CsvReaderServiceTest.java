package com.example.vaadinSolution.fundReadService;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.vaadinSolution.bo.FundValueBo;
@RunWith(SpringRunner.class)
public class CsvReaderServiceTest {
	
	@TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {
        @Bean
        public CsvReaderService employeeService() {
            return new CsvReaderService();
        }
    }
	
	@Autowired
	private CsvReaderService csvReaderService;

	/**
	 * This method tests that data is getting read from the CSV file.
	 */
	@Test
	public void whenInputRead_ExpectsData() {
		List<HashMap<String, ArrayList<FundValueBo>>> maps = null;
		maps = csvReaderService.readCSV();
		assertNotNull(maps);
	}

}
