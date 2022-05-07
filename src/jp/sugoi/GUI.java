package jp.sugoi;

class GUI {
	/*static Color  default_color=new Color(0,0,0);
	static Color blue_color =new Color(0,150,220);
	//19201080
	double golden_screen_rate=1;
	double window_screenIMG_rate=1;
	String mode="blue";
	JFrame l0=new JFrame();
	JLabel l1=new JLabel();
	ImageIcon back_img;
	JLayeredPane l2=new JLayeredPane();
	ImageIcon loading;
	JLabel desktop_label=new JLabel();
	/*
	 *___1_2__________5_6___<br>
	 *0_______3____4_______7<br>
	 *______________________<br>
	 */
	//JTextField[] ips=new JTextField[8];
	/**
	 *___4_5__________8_9___<br>
	 *3_______6____7_______10<br>
	 *____1______0_____2_____<br>
	 */
	/*
	JLabel[] debugLamp=new JLabel[11];
	HashMap<JLabel,Image> debug_info=new HashMap<>();
	JList<String> trans=new JList<>();
	ImageIcon bigIconr;
	JButton send;
	ImageIcon blue;
	JTextField to;
	JTextField amount;
	ImageIcon error;
	JTextField address;
	ImageIcon image_mini;
	ImageIcon kousi;
	JLabel show_address;
	ImageIcon send_;
	ImageIcon ok;
	JLabel copy_address;
	JLabel lab;
	int x=Toolkit.getDefaultToolkit().getScreenSize().width;
	int y=Toolkit.getDefaultToolkit().getScreenSize().height;
	double x_rate=x/(double)1920;
	double y_rate=y/(double)1080;
	public GUI() {
		System.out.println("x_rate : "+x_rate+"\r\n+y_rate : "+y_rate);
		//画像の読み込み
		try {
			ImageEditor ie=new ImageEditor();

			bigIconr = ie.make("GUI_V2_Yellow.png",0.9,0.9);
			blue =ie.make("GUI_V2_Blue.png",0.9,0.9);
			loading=ie.make("loading.png",1,1);
			back_img=ie.make("start.png", 1, 1);
			image_mini=ie.make("temp.png",1,1);
			error=ie.make("A_R.png", 0.2,0.2 );
			ok=ie.make("A_G.png", 0.2,0.2 );
			send_=ie.make("send.png", 0.9, 0.9);
			kousi = ie.make("chart.png",0.9,0.9);
		}catch(Exception e) {return;}
		if(!Main.haikei_nashi) {

			l0.setUndecorated(true);
			l0.setBackground(new Color(0,0,0,0));
			l0.setBounds(0,0,(int)(1920),(int)(1080));
			l0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			l1.setBounds(0,0,(int)(1920),(int)(1080));
			l2.addMouseListener(ev);
			l2.setName("hai");
			l2.setBackground(Color.black);
			l2.setBounds(0,0,(int)(1920),(int)(1080));
			int wi=1920;
			int he=1080;
			Dimension d=new Dimension((int)((wi/20)),(int)((he/20)));
			desktop_label.setBounds((int)(((d.width)-100)),(int)(((d.height)-40)),(int)(bigIconr.getIconWidth()),(int)(bigIconr.getIconHeight()));
			l1.add(l2);
			l0.add(l1);
			l0.setVisible(true);

			send=new JButton();
			send.setBounds((int)(1322),(int)(503),(int)(105),(int)(37));
			send.setPressedIcon(send_);
			send.setBorderPainted(false);
			send.setOpaque(false);
			send.setBackground(new Color(0,0,0,0));
			send.setActionCommand("send");
			send.addActionListener(ev);
			desktop_label.add(send);
			desktop_label.setIcon(blue);
			desktop_label.addMouseListener(ev);
			desktop_label.addMouseMotionListener(ev);
			desktop_label.setName("desktop");
			//l1.setIcon(back_img);
			l1.setBackground(new Color(0,0,0,0));
			to=to();
			desktop_label.add(to);
			amount=amount();
			desktop_label.add(amount);
			fill_ips_debug();


			debugLamp[0].setBounds((int)(940), (int)(290),(int)((error.getIconWidth()+1)),(int)((error.getIconHeight()+1)));

			debug_add();

			ips_add();


			new Thread(new Anime()).start();
		}else {

		}
	}
	void address() {
		address=new JTextField();
		address.setBounds(1151, 595,449, 27);
		address.setBackground(Color.black);
		address.setForeground(Color.white);
		address.setOpaque(false);
		LineBorder border = new LineBorder(new Color(255,255,0,0), 1, true);
		address.setBorder(border);
		address.setFont(new Font("MS P明朝",Font.BOLD,16));
		String[] s=Main.w.address_0x0a.split("0x0a");
		BigInteger bi=new BigInteger(s[0],10);
		BigInteger bi1=new BigInteger(s[1],10);
		address.setText(bi.toString(16)+"0x0a"+bi1.toString(16));
		desktop_label.add(address);

		copy_address=new JLabel();
		copy_address.setBounds(975,595,170,27);
		copy_address.setBackground(new Color(0,0,0,0));
		desktop_label.add(copy_address);

		show_address=new JLabel();
		show_address.setBounds(1600,595,170,27);
		show_address.setBackground(new Color(0,0,0,0));
		desktop_label.add(show_address);
		show_address.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				new Thread(new Graph(1)).start();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

		});
		copy_address.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("コピーした！");
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection selection = new StringSelection(address.getText());
				clipboard.setContents(selection, selection);
				Thread th=new Thread() {
					@Override
					public void run() {
						JLabel temp=new JLabel();
						temp.setText("コピーしました！");
						temp.setFont(new Font("MS P明朝",Font.PLAIN,18));
						temp.setBounds(975,565, 500, 27);
						desktop_label.add(temp);
						for(int i=255;i>=0;i--) {
							temp.setForeground(new Color(200,200,200,i));
							temp.repaint();
							try {Thread.sleep(10);}catch(Exception e) {}
						}
					}
				};
				th.start();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}
		});
	}
	JTextField amount() {
		amount= new JTextField("Enter the withdrawal amount");
		amount.setBounds((int)(1452),(int)(417),(int)(325),(int)(29));
		amount.setFont(new Font("MS P明朝",Font.ITALIC,25));
		amount.setBackground(new Color(0,0,0,0));
		amount.setForeground((mode.equals("blue")? blue_color : Color.yellow));
		amount.setOpaque(false);
		amount.setBorder(null);
		return amount;
	}
	JTextField to() {
		to=new JTextField("Please enter an address");
		to.setBounds((int)(980),(int)(417),(int)(325),(int)(29));
		to.setFont(new Font("MS P明朝",Font.ITALIC,25));
		to.setBackground(new Color(0,0,0,0));
		to.setForeground((mode.equals("blue")? blue_color : Color.yellow));
		to.setOpaque(false);
		to.setBorder(null);
		return to;
	}
	class Graph extends Thread{
		int mode=1;

		Graph(int mode){
			this.mode=mode;
		}
		@Override
		public void run() {
			JFrame address_frame=new JFrame();
			JLayeredPane jp=new JLayeredPane();//1089 619
			JLabel back=new JLabel(kousi);
			back.setBounds(0,0,(int)(kousi.getIconWidth()),(int)(kousi.getIconHeight()));
			address_frame.setBounds(0,0,(int)(kousi.getIconWidth()),(int)(kousi.getIconHeight()));
			jp.setBounds(0,0,(int)(kousi.getIconWidth()),(int)(kousi.getIconHeight()));
			address_frame.add(back);
			address_frame.setResizable(false);
			back.add(jp);
			System.out.println("run");
			address_frame.setVisible(true);
			String hash;
			for(;;) {
				try {
					hash=(this.mode==0)?to.getText().replace("0x0a",""):Main.w.address_0x0a;
				}catch(Exception e) {
					hash="";
				}
				JLabel[] jl=new JLabel[hash.length()];
				for(int a=0;a<jl.length;a++) {
					jl[a]=new JLabel();
					jl[a].setBounds(0,0,(int)(18),(int)(18));
					jl[a].setText("O");
					jl[a].setForeground(blue_color);
					jp.add(jl[a]);
				}
				try{
					int a=0;
					for(a=0;a<jl.length;a++) {
						int value = Integer.parseInt(hash.charAt(a)+"",16);
						jl[a].setBounds((int)((a*13.5)),(int)((value*6)),(int)(10),(int)(10));
						back.repaint();
						try{Thread.sleep(50);}catch(Exception e) {}
					}
				}catch(Exception e) {try{Thread.sleep(500);}catch(Exception ee) {}}
				try{Thread.sleep(50);}catch(Exception e) {}
			}
		}
	}

	class Anime extends Thread{
		@Override
		public void run() {
			try {
				Thread.sleep(800);
			} catch (InterruptedException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
			JLabel sin=new JLabel();

			JLabel l0=new JLabel();
			l0.setBounds((int)(((desktop_label.getWidth()/2)-(loading.getIconWidth()/2))),(int)(((desktop_label.getHeight()/2)-(loading.getIconHeight()/2))),(int)(loading.getIconWidth()),(int)(loading.getIconHeight()));
			l0.setIcon(loading);
			Main.gui.l1.add(l0);
			JLabel l1=new JLabel();
			l1.setSize(ok.getIconWidth(),ok.getIconHeight());
			l1.setIcon(ok);
			l0.add(l1);
			JLabel l2=new JLabel();
			l2.setSize(error.getIconWidth(),error.getIconHeight());
			l2.setIcon(error);
			l0.add(l2);
			int temp=(desktop_label.getWidth()-error.getIconWidth())/10;
			sin.setBounds((int)((temp*5)),(int)(330),(int)(500),(int)(50));
			l0.add(sin);

			int max=190;
			for(int i = 0;i<=max;i++) {
				sin.setText("Loaded    "+i+"  /  "+max);
				l1.setLocation((int)(((l0.getWidth()/max)*i)), (int)(300));
				l2.setLocation(((l0.getWidth()/max)*(max-i)), 370);
				try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
			}
			l0.remove(l1);
			l0.remove(l2);
			Main.gui.l1.remove(l0);
			Main.gui.l1.repaint();
			address();
			Main.gui.l2.add(desktop_label);
		}

	}


	void fill_ips_debug() {
		for(int i=0;i<8;i++) {
			JTextField  f=new JTextField();
			f.setMargin(new Insets(0,(int)(10),0,0));
			f.setOpaque(false);
			f.setFont(new Font("meiryo",Font.PLAIN,23));
			f.setForeground(blue_color);
			f.setHorizontalAlignment(JTextField.CENTER);
			f.setText("");
			LineBorder border = new LineBorder(new Color(255,255,0,0), 1, true);
			f.setBorder(border);
			f.setEditable(false);
			ips[i]=f;
		}
		for(int i=0;i<11;i++) {
			lab=new JLabel();
			debugLamp[i]=lab;
			debugLamp[i].setIcon(error);
			debugLamp[i].setName(i+"");
			debugLamp[i].addMouseListener(ev);
		}
	}

	void ips_add() {
		
		//-----左←
		ips[0].setBounds((int)((105)), (int)((105)),(int)((202)),(int)((49)));
		desktop_label.add(ips[0]);
		ips[1].setBounds((int)((295)), (int)((60)),(int)((202)),(int)((49)));
		desktop_label.add(ips[1]);
		ips[2].setBounds((int)((480)), (int)((60)),(int)((202)),(int)((49)));
		desktop_label.add(ips[2]);
		ips[3].setBounds((int)((670)), (int)((105)),(int)((202)),(int)((49)));
		desktop_label.add(ips[3]);
		//-----右→
		ips[4].setBounds((int)((1003)), (int)((105)),(int)((202)),(int)((49)));
		desktop_label.add(ips[4]);
		ips[5].setBounds((int)((1185)), (int)((60)),(int)((202)),(int)((49)));
		desktop_label.add(ips[5]);
		ips[6].setBounds((int)((1390)), (int)((60)),(int)((202)),(int)((49)));
		desktop_label.add(ips[6]);
		ips[7].setBounds((int)((1580)), (int)((105)),(int)((202)),(int)((49)));
		desktop_label.add(ips[7]);
	}

	void debug_add(){
		desktop_label.add(debugLamp[0]);
		debugLamp[1].setBounds((int)(480), (int)(309), (int)(error.getIconWidth()),(int)(error.getIconHeight()));
		desktop_label.add(debugLamp[1]);
		debugLamp[2].setBounds((int)1400,(int)310, (int)(error.getIconWidth()),(int)(error.getIconHeight()));
		desktop_label.add(debugLamp[2]);
		debugLamp[3].setBounds((int)(184), (int)(160),(int)(error.getIconWidth()), (int)(error.getIconHeight()));
		desktop_label.add(debugLamp[3]);
		debugLamp[4].setBounds((int)(376), (int)(110), (int)(error.getIconWidth()),(int)( error.getIconHeight()));
		desktop_label.add(debugLamp[4]);
		debugLamp[5].setBounds((int)(584), (int)(110), (int)(error.getIconWidth()), (int)(error.getIconHeight()));
		desktop_label.add(debugLamp[5]);
		debugLamp[6].setBounds((int)(766), (int)(160), (int)(error.getIconWidth()), (int)(error.getIconHeight()));
		desktop_label.add(debugLamp[6]);
	}
	EventListener ev=new EventListener();
	HashMap<JLabel,Stats> debugs=new HashMap<>();
	public void stat(int i,String name, boolean b,String[][] info) {
		if(!b) {
			debugLamp[i].setIcon(error);
			debug_info.put(debugLamp[i],error.getImage());
			debugs.put(debugLamp[i],new Stats(info));
			debugLamp[i].repaint();
		}else {
			debugLamp[i].setIcon(ok);
			debugs.put(debugLamp[i],new Stats(info));
			debug_info.put(debugLamp[i],ok.getImage());
			debugLamp[i].repaint();
		}
	}
	*/
}
