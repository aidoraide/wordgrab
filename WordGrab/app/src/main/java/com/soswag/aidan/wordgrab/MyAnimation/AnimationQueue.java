package com.soswag.aidan.wordgrab.MyAnimation;

import android.graphics.Paint;
import android.graphics.Point;

import com.soswag.aidan.wordgrab.Tile.TouchableObject;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Created by Aidan on 2016-07-15.
 *
 * NOTE : IN ANIMATION QUEUES OF LINKED ANIMATIONS, SLIDE ANIMATION MUST COME BEFORE ALL OTHERS
 *
 */
public class AnimationQueue {

    private ArrayDeque<InGameAnimation> deque;
    private TouchableObject attachedObj;

    public AnimationQueue(TouchableObject attachedObj){
        this.attachedObj = attachedObj;
        deque = new ArrayDeque<>();
    }

    public void clear(){
        deque.clear();
    }

    public void add(InGameAnimation animation){
        deque.add(animation);
    }

    public boolean tick(){
        boolean tickResult = false;

        if(!isEmpty()) {

                tickResult = deque.peekFirst().tick();

            if (!tickResult) {
                //If we remove a fade animation that means the tile can no longer be seen
                if(deque.peek() instanceof FadeAnimation)
                    attachedObj.setVisible(false);
                else if(deque.peek() instanceof LinkedAnimation)
                    if(((LinkedAnimation) deque.peek()).getPaint() != null)
                        attachedObj.setVisible(false);

                deque.remove();
            }
        }

        return tickResult;
    }

    public Paint getPaint(){
        if(deque.isEmpty())
            return null;
        return deque.peek().getPaint();
    }

    public boolean isEmpty(){return deque.isEmpty();}

    public int getRemainingTicks(){
        if(deque.isEmpty())
            return 0;
        return deque.peek().framesRemaining;
    }

    public Point getAnimEndPoint(){

        Point endPoint = null;

        for(Iterator iterator = deque.descendingIterator(); iterator.hasNext();){
            InGameAnimation anim = (InGameAnimation) iterator.next();
            endPoint = anim.getAnimEndPoint();
            if(endPoint != null)
                break;
        }
        if(endPoint == null)
            endPoint = attachedObj.getPoint();

        return endPoint;
    }

    /*public void addOverwrite(SlideResizeAnimation animationOverwrite){
        int i = 0;
        int activeAnims = 0;
        if(links.peek() != null)
            activeAnims = links.peek();
        for(Iterator itr = deque.iterator(); i < activeAnims && itr.hasNext(); i++) {
            InGameAnimation anim  = (InGameAnimation) itr.next();
            if(anim instanceof SlideResizeAnimation) {
                ((SlideResizeAnimation) anim).overwrite(animationOverwrite);
                return;
            }
        }

        add(animationOverwrite);
    }

    public void addLinkedOverwrite(InGameAnimation [] newAnimations){

        //addLinkedAnimations(animations);
        SlideResizeAnimation slide = null;

        int activeAnims = 0;
        if(links.peek() != null) {
            activeAnims = links.peek();
            links.pop();
        }

        //Find the slide anim so we can overwrite it
        for(int i = 0; i < activeAnims; i++) {
            InGameAnimation anim = deque.pop();
            if(anim instanceof SlideResizeAnimation){
                slide = (SlideResizeAnimation) anim;
            }
        }

        for (int j = 0; j < newAnimations.length; j++) {
            if (newAnimations[j] instanceof SlideResizeAnimation) {
                if(slide != null) {
                    System.out.println("Overwriting slide");
                    slide.overwrite((SlideResizeAnimation) newAnimations[j]);
                }else
                    slide = (SlideResizeAnimation) newAnimations[j];
            }else {
                deque.addFirst(newAnimations[j]);
            }

        }

        if(slide != null)
            deque.addFirst(slide);

        links.addFirst(newAnimations.length);

    }*/

    public String printClass(InGameAnimation a){
        if(a instanceof SlideResizeAnimation)
            return ("slide");
        else if(a instanceof FadeAnimation)
            return("fade");
        else if(a instanceof DelayAnimation)
            return "delay";
        else
            return("other???");
    }

    public String toString(){
        String bang = "******************************************";
        for(InGameAnimation anim : deque){
            bang += '\n' + anim.toString();
        }
        bang += "\n******************************************\n";
        return bang;
    }

}
