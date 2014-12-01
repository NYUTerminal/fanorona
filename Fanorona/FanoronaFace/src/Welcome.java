import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;


public class Welcome {

	protected Shell shell;
	private Text text;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Welcome window = new Welcome();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(765, 461);
		shell.setText("SWT Application");
		
		Label lblWelcomeToFanorona = new Label(shell, SWT.NONE);
		lblWelcomeToFanorona.setBounds(316, 27, 269, 41);
		lblWelcomeToFanorona.setText("Welcome to Fanorona");
		Button btnRadioButton = new Button(shell, SWT.RADIO);
		btnRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnRadioButton.setBounds(351, 167, 92, 18);
		btnRadioButton.setText("White");
		
		Button button = new Button(shell, SWT.RADIO);
		button.setText("Black");
		button.setBounds(475, 167, 92, 18);
		
		Label lblChooseYourColor = new Label(shell, SWT.NONE);
		lblChooseYourColor.setBounds(57, 170, 252, 14);
		lblChooseYourColor.setText("Choose your color to start the game");
		
		Label lblChooseBoardSize = new Label(shell, SWT.NONE);
		lblChooseBoardSize.setBounds(57, 249, 194, 14);
		lblChooseBoardSize.setText("Choose board size");
		
		Button btnRadioButton_1 = new Button(shell, SWT.RADIO);
		btnRadioButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnRadioButton_1.setBounds(351, 246, 92, 18);
		btnRadioButton_1.setText("3X3");
		
		Button btnRadioButton_2 = new Button(shell, SWT.RADIO);
		btnRadioButton_2.setBounds(475, 246, 92, 18);
		btnRadioButton_2.setText("5X5");
		btnRadioButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		
		Label lblEnterYourName = new Label(shell, SWT.NONE);
		lblEnterYourName.setBounds(58, 106, 193, 14);
		lblEnterYourName.setText("Enter your name");
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(348, 101, 185, 19);
		
		Button btnPlay = new Button(shell, SWT.NONE);
		btnPlay.setBounds(337, 322, 95, 28);
		btnPlay.setText("Play");

	}
}