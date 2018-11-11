package com.example.vaadinSolution.fundReadService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.vaadinSolution.bo.FundValueBo;
import com.vaadin.annotations.Theme;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shreyas Deshpande
 * 
 * This class reads data from CSV file placed in classpath:resources/InputData folder with name DATA.csv
 * Class populates read fund names in component Fund name then the date user wish to compare fund value with and end date is 
 * always populated as next dates from start date selected for that particular fund.
 * When user selects end date all price values are shown in Grid from start date to end date
 * 
 */
@SpringUI
@Theme("valo")
public class FundPriceReaderUI extends UI {

	private static final long serialVersionUID = -426060358734272254L;

	@Autowired
	private CsvReaderService csvReaderService;

	VerticalLayout verticalLayout;
	HorizontalLayout horizontalLayout;
	ComboBox<String> fundNames;
	ComboBox<String> startDates;
	ComboBox<String> endDates;
	Grid<FundValueBo> dataGrid;

	/**
	 *Overriding init() method provided by UI class
	 *delegating flow for filling up data on UI
	 */
	@Override
	protected void init(VaadinRequest request) {
		initializeComponents();
		showDataOnUi();
	}

	/**
	 * This method is for initializing components related to show data on UI
	 */
	private void initializeComponents() {
		
		horizontalLayout = new HorizontalLayout();
		horizontalLayout.setMargin(true);
		horizontalLayout.setSpacing(true);
		
		fundNames = new ComboBox<>("Fund Name");
		startDates = new ComboBox<>("Date from");
		endDates = new ComboBox<>("Date till");
		dataGrid = new Grid<>("Fund Value Prices");
	}

	/**
	 * This method takes care of getting data read by 
	 * aplication from CSV file and then populating Data step by step
	 */
	public void showDataOnUi() {

		final List<HashMap<String, ArrayList<FundValueBo>>> maps = csvReaderService.readCSV();
		List<String> companyNames = new ArrayList<>();

		populateCompanyDropdown(maps, companyNames);
		fundNames.setItems(companyNames);
		fundNames.setEmptySelectionAllowed(false);
		fundNames.addValueChangeListener(event -> {
			populateStartDateDropdown(maps, event);
		});

		horizontalLayout.addComponents(fundNames, startDates, endDates, dataGrid);
		setContent(horizontalLayout);
	}

	/**
	 * This method populates drop down box called Fund name. 
	 * this drop down box is inline with the funds available in CSV file
	 * Once Fund name is selected next dropdown is filled with dates from which we can compare fund values.
	 * 
	 * @param maps
	 * @param event
	 */
	private void populateStartDateDropdown(final List<HashMap<String, ArrayList<FundValueBo>>> maps,
			ValueChangeEvent<String> event) {
		// For every company select populate the dates in drop down
		if (event.getSource().isEmpty()) {
			Notification.show("Please select company name");
		} else {
			// We will form a two dates drop downs
			List<String> startdates = new ArrayList<>();
			for (HashMap<String, ArrayList<FundValueBo>> hashMap : maps) {
				hashMap.entrySet().forEach(entry -> {
					if (entry.getKey().equals(event.getValue())) {
						entry.getValue().forEach(value -> startdates.add(value.getDate()));
					}
				});
			}
			startDates.setEmptySelectionAllowed(false);
			startDates.setItems(startdates);
			
			// Here lets populate another date field drop down starting from the selected
			// start date
			startDates.addValueChangeListener(startDateEvent -> {
				populateEndDateDropdown(maps, event, startDateEvent);
			});
		}
		resetDataGrid();
	}

	/**
	 * This method removes all columns and data from Grid panel whenever user does change fund name or start date.
	 * so that new result will be shown once user selects fund name start date and end date
	 * 
	 */
	private void resetDataGrid() {
		dataGrid.removeAllColumns();
		dataGrid.setItems(new ArrayList());
	}

	/**
	 * This method populates Till Date drop down.
	 * This drop down will always contain next dates from the dates selected from 'Date from' drop down
	 * @param maps
	 * @param event
	 * @param startDateEvent
	 */
	private void populateEndDateDropdown(final List<HashMap<String, ArrayList<FundValueBo>>> maps,
			ValueChangeEvent<String> event, ValueChangeEvent<String> startDateEvent) {
		List<String> enddates = new ArrayList<>();
		if(startDateEvent.getSource().isEmpty()) {
			Notification.show("Please select start date");
		}
		for (HashMap<String, ArrayList<FundValueBo>> hashMap : maps) {
			hashMap.entrySet().forEach(entry -> {
				if (entry.getKey().equals(event.getValue())) {
					entry.getValue().stream().filter(
							v -> LocalDate.parse(v.getDate())
									.isAfter(LocalDate.parse(startDateEvent.getValue()))
					).forEach(value -> enddates.add(value.getDate()));
				}
			});
		}
		endDates.setEmptySelectionAllowed(false);
		endDates.setItems(enddates);
		endDates.addValueChangeListener(endDateEvent -> {
			populateGridWithSelectedDateRangeValues(maps, event, startDateEvent, endDateEvent);

		});
		dataGrid.removeAllColumns();
		dataGrid.setItems(new ArrayList<>());
	}

	/**
	 * This method populates Grid panel with the selection criteria as 'Fund name'->'Date From'->'Date till'
	 * Here Dates are inclusive
	 * @param maps
	 * @param event
	 * @param startDateEvent
	 * @param endDateEvent
	 */
	private void populateGridWithSelectedDateRangeValues(final List<HashMap<String, ArrayList<FundValueBo>>> maps,
			ValueChangeEvent<String> event, ValueChangeEvent<String> startDateEvent,
			ValueChangeEvent<String> endDateEvent) {
		resetDataGrid();
		
		if(endDateEvent.getSource().isEmpty()) {
			Notification.show("Please select end date");
		}
		
		for (HashMap<String, ArrayList<FundValueBo>> hashMap : maps) {
			hashMap.entrySet().forEach(entry -> {
				if (entry.getKey().equals(event.getValue())) {
					dataGrid.setItems(entry.getValue().stream()
							.filter(v -> ((LocalDate.parse(v.getDate())
									.isAfter(LocalDate.parse(startDateEvent.getValue())))
									|| (LocalDate.parse(v.getDate())
											.isEqual(LocalDate.parse(startDateEvent.getValue()))))
									&& (LocalDate.parse(v.getDate())
											.isBefore(LocalDate.parse(endDateEvent.getValue()))
											|| (LocalDate.parse(v.getDate())
													.isEqual(LocalDate.parse(endDateEvent.getValue()))))

					));

					dataGrid.addColumn(FundValueBo::getDate).setCaption("Date");
					dataGrid.addColumn(FundValueBo::getValue).setCaption("Value");
				}
			});
		}
	}

	/**
	 * This method will populate Fund company names drop down values
	 * @param maps
	 * @param companyNames
	 */
	private void populateCompanyDropdown(final List<HashMap<String, ArrayList<FundValueBo>>> maps,
			List<String> companyNames) {
		for (HashMap<String, ArrayList<FundValueBo>> hashMap : maps) {
			hashMap.entrySet().forEach(entry -> {
				companyNames.add(entry.getKey());
			});
		}
	}
	
}
