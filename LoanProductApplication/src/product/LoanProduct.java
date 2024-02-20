package product;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import databaseConnector.LoanProductSQL;
import utils.Constants;
import utils.Util;

import java.util.ArrayList;

public class LoanProduct extends Product {

	protected int loanId;
	protected double rate;
	protected double totalValue;
	protected List<Schedule> disbursementSchedule;
	protected Schedule schedule;
	protected String paymentOption;

	public LoanProduct() {

	}

	public LoanProduct(int productId, int loanId, Date startDate, Date endDate, String productType, double totalValue,
			double rate, List<Schedule> disbursementSchedule, ArrayList<Cashflow> cashflows, String paymentOption) {

		super(productId, productType, startDate, endDate, cashflows);
		this.loanId = loanId;
		this.rate = rate;
		this.totalValue = totalValue;
		this.disbursementSchedule = disbursementSchedule;
		this.paymentOption = paymentOption;

	}

	public LoanProduct(Product p, int loanId, double totalValue, double rate, List<Schedule> disbursementSchedule,
			String paymentOption) {

		super(p);
		this.loanId = loanId;
		this.rate = rate;
		this.totalValue = totalValue;
		this.disbursementSchedule = disbursementSchedule;
		this.paymentOption = paymentOption;

	}

	public int getLoanId() {
		return loanId;
	}

	public void setLoanId(int loanId) {
		this.loanId = loanId;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(Double totalValue) {
		this.totalValue = totalValue;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public List<Schedule> getDisbursementSchedule() {

		return disbursementSchedule;
	}

	public void setDisbursementSchedule(List<Schedule> disbursementSchedule) {
		this.disbursementSchedule = disbursementSchedule;
	}

	// Method prints disbursement schedule
	public String printDisbursementSchedule() {
		String str = new String();
		str += "{";
		for (Schedule s : disbursementSchedule) {
			str += s.toString() + ",";
		}
		str += "}";
		return str;
	}

	/**
	 * updates product details using the product id given by user and the
	 * details user want to update
	 */

	@Override
	public void updateProduct(int productId) {
		try {
			LoanProductSQL.updateProduct(productId);

		} catch (Exception e) {
			System.out.println("Product with product Id " + productId + "does not exist");
		}

	}

	/**
	 * creates the product using the product details given by user
	 */
	@Override
	public void createProduct() {

		try {
			LoanProductSQL.insertLoanProduct(this);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * Returns the product details using the productId
	 */
	@Override
	public Product readProduct(int id) {
		Product loanProduct = null;
		try {

			loanProduct = LoanProductSQL.readProduct(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return loanProduct;
	}

	/**
	 * Deletes product by using productId
	 */
	@Override
	public void deleteProduct(int ProductId) {

		try {

			LoanProductSQL.deleteProduct(ProductId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getCashflow(int productId) {
		Product loanProduct = (LoanProduct) LoanProductSQL.readProduct(productId);
		Cashflow cs = new LoanCashflow();
		ArrayList<Cashflow> cashflows = cs.generateCashflows(loanProduct);
		loanProduct.setCashflows(cashflows);
		for (int i = 0; i < cashflows.size(); i++) {
			LoanCashflow cashflow = (LoanCashflow) cashflows.get(i);
			System.out.println(cashflow);
		}

	}

	public Product buildProduct(HashMap<String, String> inputs) throws Exception {
		try {
			String productType = inputs.get(Constants.PRODUCTTYPE);
			Date startDate = Util.parseDate(inputs.get(Constants.STARTDATE));
			Date endDate = Util.parseDate(inputs.get(Constants.ENDDATE));

			double rate = Double.parseDouble(inputs.get(Constants.RATE));
			double totalValue = Double.parseDouble(inputs.get(Constants.TOTALVALUE));
			String schedule = inputs.get(Constants.SCHEDULE);
			String paymentOption = inputs.get(Constants.PAYMENTOPTION);
			String[] scheduleArray = schedule.split("\\_");
			List<Schedule> disbursementSchedule = new ArrayList<Schedule>();
			for (String s : scheduleArray) {
				Schedule sch = new Schedule();
				String[] oneSchedule = s.split("=");
				sch.setDate(Util.parseDate(oneSchedule[0]));
				sch.setAmount(Double.parseDouble(oneSchedule[1]));
				disbursementSchedule.add(sch);
			}
			Product.inputValidation(startDate, endDate, totalValue, rate, disbursementSchedule);

			LoanProduct loanProduct = new LoanProduct(-1, -1, startDate, endDate, productType, totalValue, rate,
					disbursementSchedule, null, paymentOption);
			return loanProduct;
		} catch (Exception e) {
			System.err.print(Constants.DETAILSERROR);
			System.exit(0);
		}
		return null;
	}

	// Method for printing the loan product details
	@Override
	public String toString() {
		return "LoanProduct{" +

				"productId=" + this.getProductId() + ", loanId=" + getLoanId() + ", rate=" + rate + ",Start Date="
				+ Util.formatDate(getStartDate()) + ",End Date=" + Util.formatDate(getEndDate()) + ", totalValue="
				+ totalValue + ", disbursementSchedule= " + disbursementSchedule + " ," + "Payment Option: "
				+ paymentOption + '}';
	}

	public String getPaymentOption() {
		return paymentOption;
	}

	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
	}

}