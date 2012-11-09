package com.android.calendar;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class Cal extends Activity implements OnClickListener,
		OnItemClickListener {

	// 1週間の日数
	private int DAY_WEEK = 7;
	// Gridviewのインスタンス
	private GridView GridView = null;
	// DateCellAdapterのインスタンス
	private DateCellAdapter DateCellAdapter = null;
	// 現在注目している年月日を保持する変数
	private GregorianCalendar mCalendar = null;
	// カレンダーの年月を表示するTextView
	private TextView dateText = null;
	// 前月ボタンのインスタンス
	private Button prevButton = null;
	// 次月ボタンのインスタンス
	private Button nextButton = null;
	// 検索ボタンのインスタンス
	private Button searchButton = null;

	private Calendar calendar = Calendar.getInstance();
	private int year;
	private int month;

	private RegistDao registDao;
	private SQLiteDatabase db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		DatabaseHelper dbHelper = new DatabaseHelper(this);
		db = dbHelper.getReadableDatabase();

		// Helperクラスからdbを取得したものをDaoのコンストラクタに設定
		registDao = new RegistDao(db);

		// 「GregorianCalendar」のインスタンスの作成
		mCalendar = new GregorianCalendar();
		// 年月の取得
		year = mCalendar.get(Calendar.YEAR);
		month = mCalendar.get(Calendar.MONTH) + 1;

		GridView = (GridView) findViewById(R.id.gridView1);
		// Gridカラム数を設定する
		GridView.setNumColumns(DAY_WEEK);
		// DateCellAdapterのインスタンスを作成する
		DateCellAdapter = new DateCellAdapter(this);
		// GridViewに「DateCellAdapter」をセット
		GridView.setAdapter(DateCellAdapter);
		GridView.setOnItemClickListener(this);

		// 年月のビューへの表示
		dateText = (TextView) findViewById(R.id.dateText);
		dateText.setText(year + "年" + month + "月");
		// 前月ボタンにListenerを設定
		prevButton = (Button) findViewById(R.id.prevMonth);
		prevButton.setOnClickListener(this);
		// 次月ボタンにListenerを設定
		nextButton = (Button) findViewById(R.id.nextMonth);
		nextButton.setOnClickListener(this);
		// 検索ボタンにListenerを設定
		searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				 Intent intent = new Intent();
				 intent.setClass(Cal.this, Search.class);
				 intent.setAction(Intent.ACTION_VIEW);
				 startActivity(intent);
			}
		});
	}

	public class DateCellAdapter extends BaseAdapter {

		// 縦（ROW）の数
		private int NUM_ROWS = 6;
		// 画面に表示する日付の数
		private int NUM_CELLS = DAY_WEEK * NUM_ROWS;
		private LayoutInflater mLayoutInflater = null;

		/**
		 * コンストラクタではパラメタで受け取ったcontextを使用して 「LayoutInflater」のインスタンスを作成する
		 * 
		 * @param contextアクティビティ
		 */
		DateCellAdapter(Context context) {
			// getSystemServiceでContextからLayoutInflaterを取得
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		// getCount「NUM_OF_CELLS」(42)を返す
		@Override
		public int getCount() {
			return NUM_CELLS;
		}

		// getItem必要ないのでnullを返す
		@Override
		public Object getItem(int arg0) {
			return null;
		}

		// getItemId必要ないので0を返す
		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.datecell, null);
			}

			// Viewの最小の高さを設定する
			convertView.setMinimumHeight(parent.getHeight() / NUM_ROWS - 1);
			TextView dayOfMonthView = (TextView) convertView
					.findViewById(R.id.dayOfMonth);

			calendar = (Calendar) mCalendar.clone();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.DAY_OF_MONTH,
					position - calendar.get(Calendar.DAY_OF_WEEK) + 1);

			dayOfMonthView.setText("" + calendar.get(Calendar.DAY_OF_MONTH));
			TextView scheduleView = (TextView) convertView
					.findViewById(R.id.schedule);

			if (position % 7 == 0) {
				dayOfMonthView.setBackgroundResource(R.color.red);
			} else if (position % 7 == 6) {
				dayOfMonthView.setBackgroundResource(R.color.blue);
			} else {
				dayOfMonthView.setBackgroundResource(R.color.gray);
			}

			scheduleView.setText("");
			return convertView;
		}
	}

	/**
	 * onClick 前月、次月ボタンでクリックされたとき呼び出される
	 */
	public void onClick(View v) {

		// 現在の注目している日付を当月の1日に変更する
		mCalendar.set(Calendar.DAY_OF_MONTH, 1);
		if (v == prevButton) {
			// 1か月減算する
			mCalendar.add(Calendar.MONTH, -1);
		} else if (v == nextButton) {
			// 1か月加算する
			mCalendar.add(Calendar.MONTH, 1);
		}

		//新たな年月を表示される
		dateText.setText(mCalendar.get(Calendar.YEAR) + "年"
				+ (mCalendar.get(Calendar.MONTH) + 1) + "月");
		DateCellAdapter.notifyDataSetChanged();
	}

	/**
	 * onItemClick　日がクリックされたとき呼び出される
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

		// カレンダーをコピー
		Calendar cal = (Calendar) mCalendar.clone();

		// positionから日付を計算
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, position - cal.get(Calendar.DAY_OF_WEEK)
				+ 1);

		//月日を変数に代入
		String Month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		Integer day = Integer
				.valueOf(EvenInfo.dateFormat.format(cal.getTime()));
		String Day = String.valueOf(day);

		// 月日を渡して該当する有名人を検索
		List<RegistData> registList = registDao.findAll(Month, Day);

		// 検索した有名人をセットする
		RegistDataList registdatalist = new RegistDataList();
		for (RegistData registdata : registList) {
			registdatalist.setRegistDataList(registdata);
		}

		// Intentを作成
		Intent intent = new Intent(Cal.this, BirthList.class);
		intent.putExtra("MONTH", String.valueOf(Month));
		intent.putExtra("DAY", String.valueOf(Day));
		intent.putExtra("RegistDataList", registdatalist);
		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);
	}
}
