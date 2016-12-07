package hons.segd.androidgame;


import android.graphics.Bitmap;

import java.util.Vector;

public class Level {

    private Vector<ElementObj> elementContainer;
    private int nextElementPos; //index position for next element added

    public Level()
    {
        nextElementPos = 0;
        elementContainer = new Vector<ElementObj>();
    }

    public void Update(Player playerActor)
    {
        for(ElementObj e : elementContainer)
        {
            e.Update();
        }

        if(playerActor.GetActivating() == true) //this will equal true when player presses activate
        {
            for(ElementObj e : elementContainer)
            {
                if(e.GetFacingDirection() == playerActor.GetFacingDirection())
                {
                    if(e.IsPlayerTouching(playerActor.getX(),playerActor.getY(),playerActor.width,playerActor.height))
                    {
                        e.SetActivationState(true);
                        if(e.GetRemoveOnActivate())
                        {
                            e.SetEnabled(false);
                            if(e.GetUniqueID()==1)//water glass
                            {
                                //playerActor.SetHasWaterGlass(true);
                            }
                            else if(e.GetUniqueID()==2)//hairdryer
                            {
                                //playerActor.SetHasHairDryer(true);
                            }
                        }
                        playerActor.SetActivating(false); //reset player activation
                        break; //break from loop
                    }
                    else e.SetActivationState(false);
                }
                else e.SetActivationState(false);
            }
            playerActor.SetActivating(false);
        }
    }

    public void RenderElements(int currentOffsetX, int currentOffsetY)
    {
        for(ElementObj e : elementContainer)
        {
            if(e.GetRendered())
                e.Draw(currentOffsetX,currentOffsetY);
        }

    }

    public ElementObj GetElement(int index) {return elementContainer.get(index);}
    int GetNextElementPos() {return nextElementPos;}

    //create new element and add it to the vector
    //facingDirection variable should match player's facing direction, not elements
    public void CreateElement(int x, int y, int width, int height,
                              int facingDirection, int activationDistance, boolean rendered,
                              boolean startAnimated, Bitmap tex, boolean hasCollision,
                              boolean removeOnActivate, int uniqueID)
    {
        ElementObj newElement = new ElementObj();
        newElement.SetX(x);
        newElement.SetY(y);
        newElement.SetWidth(width);
        newElement.SetHeight(height);
        newElement.SetFacingDirection(facingDirection);
        newElement.SetActivationDistance(activationDistance);
        newElement.SetVectorPos(nextElementPos);
        if(!startAnimated)
            newElement.SetTexture(tex);//use for idle textures, for static objects
        newElement.SetHasCollision(hasCollision);
        newElement.SetRemoveOnActivate(removeOnActivate);
        newElement.SetUniqueID(uniqueID);
        if(startAnimated)
            newElement.StartAnim(tex);//use for animated textures
        if(rendered)
            newElement.SetRendered(true);
        nextElementPos++;
        elementContainer.add(newElement);
    }

    //for no textures
    public void CreateElement(int x,int y,int width,int height,
                              int facingDirection,int activationDistance, boolean hasCollision)
    {
        ElementObj newElement = new ElementObj();
        newElement.SetX(x);
        newElement.SetY(y);
        newElement.SetWidth(width);
        newElement.SetHeight(height);
        newElement.SetFacingDirection(facingDirection);
        newElement.SetActivationDistance(activationDistance);
        newElement.SetVectorPos(nextElementPos);
        newElement.SetHasCollision(hasCollision);
        newElement.SetRendered(false);
        nextElementPos++;
        elementContainer.add(newElement);
    }

    public void ClearElements()
    {
        elementContainer.clear();
    }

}
