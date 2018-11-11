package com.example.vaadinSolution.fundReadService;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.example.vaadinSolution.bo.FundValueBo;

/** This service class reads/parse the CSV and stores data in the form of List<HashMap<String, ArrayList<FundValueBo>>>
 * where every key in the map is the Fund Name and all its values are stored as a list of FundValueBo objects
 */
@Component
public class CsvReaderService {
	private static final Logger LOGGER = Logger.getLogger(CsvReaderService.class.getName());
	public static final String FOLDER_NAME ="InputData";
	public static final String FILE_NAME ="DATA.csv";

	/**
	 * This method reads/parse the CSV and stores data in the form of List<HashMap<String, ArrayList<FundValueBo>>>
	 * where every key in the map is the Fund Name and all its values are stored as a list of FundValueBo objects 
	 * @return List<HashMap<String, ArrayList<FundValueBo>>> - Data structure for CSV read value
	 */
	public List<HashMap<String, ArrayList<FundValueBo>>> readCSV() {
		File file;
		Scanner inputStream = null;
		boolean isFirstLine = true;
		
		ArrayList<String> fundNames = new ArrayList<>();
		final List<HashMap<String, ArrayList<FundValueBo>>> maps = new ArrayList<>();
		try {
			file = ResourceUtils.getFile("classpath:"+FOLDER_NAME+"/"+FILE_NAME);
			inputStream = new Scanner(file);
			while (inputStream.hasNext()) {
				String data = inputStream.next();
				String[] values = data.split(",");
				for (int valueCounter = 0; valueCounter < values.length; valueCounter++) {
					if (isFirstLine && (values[valueCounter] != null && !values[valueCounter].isEmpty())) {
						fundNames.add(values[valueCounter]);
					}
				}
				if (isFirstLine) {
					initializeFundNameMap(fundNames, maps);
				}
				if (!isFirstLine) {
					populateMapValues(fundNames, maps, values);
				}
				isFirstLine = false;
			}
		} catch (FileNotFoundException fnfe) {
			LOGGER.severe("File not Found!"+fnfe.getLocalizedMessage());
		} finally {
			inputStream.close();
		}
		return maps;
	}

	/**
	 * This method populates map with values (List<FundValueBo>)
	 * Each FundValueBO represents per day price of Fund 
	 * @param fundNames
	 * @param maps
	 * @param values
	 */
	private void populateMapValues(ArrayList<String> fundNames,
			final List<HashMap<String, ArrayList<FundValueBo>>> maps, String[] values) {
		for (int fundNameCounter = 0; fundNameCounter < fundNames.size(); fundNameCounter++) {
			FundValueBo fundValue = new FundValueBo(values[0], values[fundNameCounter + 1]);
			maps.get(fundNameCounter).get(fundNames.get(fundNameCounter)).add(fundValue);
		}
	}

	/**
	 * This method will initialize map as fund name(Key). 
	 * We will populate value as a list of FundValueBo objects once we are reading data
	 *
	 * @param fundNames
	 * @param maps
	 */
	private void initializeFundNameMap(ArrayList<String> fundNames,
			final List<HashMap<String, ArrayList<FundValueBo>>> maps) {
		for (int fundNameCounter = 0; fundNameCounter < fundNames.size(); fundNameCounter++) {
			HashMap<String, ArrayList<FundValueBo>> map = new HashMap<>();
			map.put(fundNames.get(fundNameCounter), new ArrayList<>());
			maps.add(map);
		}
	}
}
