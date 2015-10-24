import java.awt.EventQueue;
import java.awt.Font;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import sun.awt.HorizBagLayout;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Design {
	
	
	private int instpercycle;
	private int entrysize;
	private int reorderbuffersize;
	private int storebuffer;
	private int addlatency;
	private int mullatency;
	private int loadlatency;
	private int storelatency;
	private int branchlatency;
	
	private int RSfreeSlots;
	
	private JLabel lblIftxt;
	private JLabel lblIdtxt;
	private JLabel lblRdtxt;
	private JButton btnStart;
	private JButton btnNext;
	private JButton btnQuit;
	private JLabel lblcompletetxt;
	private JLabel lblretiretxt;

	private int pc;
	private JFrame frame;
	
	private ArrayList<JLabel> reslabelmaker = new ArrayList<JLabel> ();
	private ArrayList<JLabel> busylabelmaker = new ArrayList<JLabel> ();
	private ArrayList<JLabel> validlabelmaker = new ArrayList<JLabel> ();
	private JLabel lblReservationStation;
	
	private int InstructionCount;
	private ArrayList<String> InstructionSet  = new ArrayList <String>();
	private ArrayList<String> DecodeInstSet = new ArrayList <String>();
	private ArrayList<String> reserveinst = new ArrayList<String>();
	private ArrayList <Integer> stagepc = new ArrayList<Integer>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Design window = new Design();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public Design() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("src/input.txt"));		
        String line =  null;
        ArrayList<String> values=new ArrayList<String>();
		while((line=br.readLine())!=null){   
		   values.add(line);
		}
		br.close();
		
		InstructionCount=0;
		br = new BufferedReader(new FileReader("src/Pr_test1.b"));		
        line =  null;
		while((line=br.readLine())!=null){
		   InstructionSet.add(line);
		   decode idinst = new decode(line);
		   String temp =idinst.getResult();
		   DecodeInstSet.add(temp);
		   InstructionCount++;
		}
		br.close();
		
		System.out.println(DecodeInstSet);
		
		instpercycle= Integer.parseInt(values.get(0)) ;
		entrysize=Integer.parseInt(values.get(1)) ;
		reorderbuffersize=Integer.parseInt(values.get(2)) ;
		storebuffer=Integer.parseInt(values.get(3)) ;
		addlatency=Integer.parseInt(values.get(4)) ;
		mullatency=Integer.parseInt(values.get(5)) ;
		loadlatency=Integer.parseInt(values.get(6)) ;
		storelatency=Integer.parseInt(values.get(7)) ;
		branchlatency=Integer.parseInt(values.get(8)) ;
		
		RSfreeSlots=entrysize;
				
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	
	private void mynextfunc(){
		Update_the_pc();
		Instruction_Fetch(stagepc.get(0));
		Instruction_Decode(stagepc.get(1));
		Read(stagepc.get(2));
		Reservation_Station(stagepc.get(3));
		Complete(stagepc.get(4));
		Retire(stagepc.get(5));
	}
	
	
	private void myfunc(){
		
		for(int i=0;i<6;i++){
			stagepc.add(-1);
		}
		pc=0;
		btnStart.setEnabled(false);
		btnNext.setEnabled(true);
		mynextfunc();
	} 
	
	
	
	private void Update_the_pc (){
		//if regstation is full wait else send
		
		stagepc.remove(stagepc.size()-1);
		if(pc<InstructionCount){
			stagepc.add(0,pc);
			pc=pc+instpercycle;
		}
		else{
			stagepc.add(0,-1);
		}	
		System.out.println(stagepc);
	}
	
	
	private void Instruction_Fetch(int curpc){		
		String temp="";
		if(curpc!=-1){			
			for(int i=0;i<instpercycle;i++){
				String insttemp="";			
				if (curpc+i < InstructionCount )
					insttemp=InstructionSet.get(curpc+i);
				
				if(i!=0)
					temp=temp+"    "+insttemp;
				else{
					temp=temp+insttemp;
				}
			}
		}
		System.out.println(temp);
		lblIftxt.setText(temp);
	}
	private void Instruction_Decode(int curpc){
		String temp="";
		if(curpc!=-1){			
			for(int i=0;i<instpercycle;i++){
				String insttemp="";			
				if (curpc+i < InstructionCount )
					insttemp=DecodeInstSet.get(curpc+i);
				
				if(i!=0)
					temp=temp+"    "+insttemp;
				else{
					temp=temp+insttemp;
				}
			}
		}
		System.out.println(temp);
		lblIdtxt.setText(temp);
	}
	private void Read(int curpc){
		if(curpc==-1){
			lblRdtxt.setText("");
		}else{
			String temp="";
			for(int i=0;i<instpercycle;i++){
				String insttemp="";			
				if (curpc+i < InstructionCount )
					insttemp=DecodeInstSet.get(curpc+i);
				
				if(i!=0)
					temp=temp+"    "+insttemp;
				else{
					temp=temp+insttemp;
				}
			}
			if(RSfreeSlots>=instpercycle){
				lblRdtxt.setText("Dispatching "+temp);
			}else{
				int numb=instpercycle-RSfreeSlots;
				String[] array = temp.split("    ");
				String newtemp="";
			    for(int i=0;i<RSfreeSlots;i++){					
					if(i!=0)
						newtemp=newtemp+"    "+array[i];
					else{
						newtemp=newtemp+array[i];
					}			    	
			    }
			    lblRdtxt.setText("Dispatching "+newtemp);
			    pc=pc-instpercycle+numb;
			}
			
		}
	}
	private void Reservation_Station(int curpc){
		/*while(reserveinst.size()<entrysize){
			
		}*/
	}
	private void Complete(int pc){}
	private void Retire(int pc){}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(119, 136, 153));
		frame.setBounds(100, 100, 1350, 725);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frame.setVisible(true);
		
		
		 btnStart = new JButton("Start");
			btnStart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					myfunc();
				}
			});
			btnStart.setBounds(732, 668, 87, 25);
			frame.getContentPane().add(btnStart);
			
			 btnNext = new JButton("Next");
			 btnNext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					mynextfunc();
					/*if(!sixqueue.isEmpty())
					mynextfunc();
					else{
						btnNext.setEnabled(false);
					}*/
				}
			});
			 btnNext.setEnabled(false);
			 btnNext.setBounds(652, 668, 87, 25);
			frame.getContentPane().add(btnNext);
			
			
			 btnQuit = new JButton("Exit");
			 btnQuit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					frame.dispose();
				}
			});
			 btnQuit.setEnabled(false);
			 btnQuit.setBounds(567, 668, 87, 25);
			frame.getContentPane().add(btnQuit);
		
		JLabel lblHead= new JLabel("Superscalar Pipelined Processor");
		lblHead.setFont(new Font("Abyssinica SIL", Font.BOLD, 28));
		lblHead.setBounds(500, 30, 600, 40);
		frame.getContentPane().add(lblHead);
		
		JLabel lblIf = new JLabel("Instruction Fetch");
		lblIf.setBounds(200, 100, 200, 15);
		frame.getContentPane().add(lblIf);
		
		JLabel lblId= new JLabel("Instructon Decode");
		lblId.setBounds(200, 150, 200, 15);
		frame.getContentPane().add(lblId);
		
		JLabel lblRd= new JLabel("Read/Dispatch");
		lblRd.setBounds(200, 200, 200, 15);
		frame.getContentPane().add(lblRd);
	
		lblIftxt = new JLabel("");
		lblIftxt.setOpaque(true);
		lblIftxt.setBackground(Color.white);
		lblIftxt.setBounds(365, 95, 774, 25);
		frame.getContentPane().add(lblIftxt);
		
		lblIdtxt = new JLabel("");
		lblIdtxt.setOpaque(true);
		lblIdtxt.setBackground(Color.white);
		lblIdtxt.setBounds(365, 145, 774, 25);
		frame.getContentPane().add(lblIdtxt);
		
		lblRdtxt = new JLabel("");
		lblRdtxt.setOpaque(true);
		lblRdtxt.setBackground(Color.white);
		lblRdtxt.setBounds(365, 195, 774, 25);
		frame.getContentPane().add(lblRdtxt);
			
		int xcord=550;
		int ycord=275;
		int curx= 330;
		int cury=310;
	
		for(int i=0;i<entrysize;i++){
			
			JLabel lblbusytxt = new JLabel("");
			lblbusytxt.setOpaque(true);
			lblbusytxt.setBackground(Color.white);
			lblbusytxt.setBounds(xcord, ycord, 50, 25);
			frame.getContentPane().add(lblbusytxt);
			
			JLabel lblrestxt = new JLabel("");
			lblrestxt.setOpaque(true);
			lblrestxt.setBackground(Color.white);
			lblrestxt.setBounds(xcord+53, ycord, 200, 25);
			frame.getContentPane().add(lblrestxt);
			
			JLabel lblvaltxt = new JLabel("");
			lblvaltxt.setOpaque(true);
			lblvaltxt.setBackground(Color.white);
			lblvaltxt.setBounds(xcord+256, ycord, 50, 25);
			frame.getContentPane().add(lblvaltxt);
			
			validlabelmaker.add(i, lblvaltxt);
			reslabelmaker.add(i, lblrestxt);
			busylabelmaker.add(i, lblbusytxt);
			
			ycord=ycord+30;
		}
		
	
		if (ycord<450)
			ycord=450;
		else{
			ycord=ycord+10;
		}
		
		
		JLabel lblMem = new JLabel("Complete");
		lblMem.setBounds(200, ycord+5, 200, 15);
		frame.getContentPane().add(lblMem);
		
		JLabel lblWb= new JLabel("Retire");
		lblWb.setBounds(200, ycord+55, 200, 15);
		frame.getContentPane().add(lblWb);
		
		lblcompletetxt = new JLabel("");
		lblcompletetxt.setOpaque(true);
		lblcompletetxt.setBackground(Color.white);
		lblcompletetxt.setBounds(365, ycord, 774, 25);
		frame.getContentPane().add(lblcompletetxt);
		
		lblretiretxt = new JLabel("");
		lblretiretxt.setOpaque(true);
		lblretiretxt.setBackground(Color.white);
		lblretiretxt.setBounds(365, ycord+50, 774, 25);
		frame.getContentPane().add(lblretiretxt);
		
		lblReservationStation = new JLabel("Reservation Station");
		lblReservationStation.setFont(new Font("Dialog", Font.BOLD, 14));
		lblReservationStation.setBounds(625, 176, 200, 50);
		frame.getContentPane().add(lblReservationStation);
			
		
	}

	private static class __Tmp {
		private static javax.swing.JTextField textField;
		private static void __tmp() {
			  javax.swing.JPanel __wbp_panel = new javax.swing.JPanel();
			  
			  textField = new javax.swing.JTextField();
			  __wbp_panel.add(textField);
			  textField.setColumns(10);
		}
	}
}