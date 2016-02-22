package com.pmdm.VideoCam;

import java.io.File;

import com.msi.manning.chapter10.VideoCam.R;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoCam extends Activity implements SurfaceHolder.Callback {

        private MediaRecorder recorder = null;
        private static final String OUTPUT_FILE = "/sdcard/uatestvideo.mp4"; //fichero de salida de grabación
        private static final String TAG = "VideoCam";
        private VideoView videoView = null;
        private ImageButton startBtn = null;
        private ImageButton playRecordingBtn = null;
        private Boolean playing = false; //controla reproducción
        private Boolean recording = false; //controla grabación
  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Referencias a las vistas de la interfaz
        startBtn = (ImageButton) findViewById(R.id.bgnBtn);
        playRecordingBtn = (ImageButton) findViewById(R.id.playRecordingBtn);      
        videoView = (VideoView)findViewById(R.id.videoView);

       //configuración del objeto SurfaceHolder
        final SurfaceHolder holder = videoView.getHolder();
        holder.addCallback(this); //registro del objeto
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //tipo de holder

       //Al pulsar BOTóN GRABAR
        startBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
               //si no hay grabación ni reproducción en curso
                if(!recording & !playing)
                {
                    try 
                    {
                        beginRecording(holder); //llama al método que comienza grabación
                        playing=false;
                        recording=true;
                        startBtn.setBackgroundResource(R.drawable.stop);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                        e.printStackTrace();
                    }
                }//si hay grabación en curso
                else if(recording) 
                {
                    try
                    {
                        stopRecording(); //se para grabación
                        playing = false;
                        recording= false;
                        startBtn.setBackgroundResource(R.drawable.play);
                    }catch (Exception e) {
                        Log.e(TAG, e.toString());
                        e.printStackTrace();
                    }
                }
            }
        });
        //al pulsar BOTóN REPRODUCIR
        playRecordingBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View view) 
            {  
            	//si no hay reproducción ni grabación en curso
              if(!playing & !recording)
                {
                    try 
                    {                     	
                        playRecording(); //llama al método para inicar REPRODUCCIÓN
                        playing=true; 
                        recording=false; 
                        playRecordingBtn.setBackgroundResource(R.drawable.stop);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                        e.printStackTrace();
                    }
                }//si hay reproducción en curso
                else if(VideoCam.this.playing) 
                {
                    try
                    {
                        stopPlayingRecording(); //parar reproducción
                        playing = false;
                        recording= false;
                        playRecordingBtn.setBackgroundResource(R.drawable.play);
                    }catch (Exception e) {
                        Log.e(TAG, e.toString());
                        e.printStackTrace();
                    }
                }

            }
        });

    }
     //para conectar la cámara con la vista
    public void surfaceCreated(SurfaceHolder holder) {
        startBtn.setEnabled(true);
    }
     //se puede usar para liberar la cámara
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
     //configura cambio de visualización, como rotar la imagen
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
        Log.v(TAG, "Width x Height = " + width + "x" + height);
    }
    //métdo para iniciar grabación
    private void playRecording() {
        MediaController mc = new MediaController(this);
        videoView.setMediaController(mc);
        videoView.setVideoPath(OUTPUT_FILE);
        videoView.start();
    }
   //método para finalizar reproducción
    private void stopPlayingRecording() {
        videoView.stopPlayback();
    }
  //método para finalizar grabación
    private void stopRecording() throws Exception {
        if (recorder != null) {
            recorder.stop();
        }
    }
    //al finalizar ciclo vida
    protected void onDestroy() {
        super.onDestroy();
        if (recorder != null) {
            recorder.release();
        }
    }
     //método para iniciar la grabación
    private void beginRecording(SurfaceHolder holder) throws Exception {
        if(recorder!=null)
        {
            recorder.stop();
            recorder.release();
        }
        //fichero de salida de grabación
        File outFile = new File(OUTPUT_FILE);
        if(outFile.exists())
        {
            outFile.delete();
        }

        try {
            recorder = new MediaRecorder();
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setVideoSize(320, 240);
            recorder.setVideoFrameRate(15);
            recorder.setMaxDuration(20000); 
            recorder.setPreviewDisplay(holder.getSurface());
            recorder.setOutputFile(OUTPUT_FILE);
            recorder.prepare();
            recorder.start();
        }
        catch(Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }
}