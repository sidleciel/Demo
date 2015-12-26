package com.rhodes.demo.Util;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.util.Log;
import android.view.InputDevice;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by xiet on 2015/6/18.
 */
public class ClassUtil {

    //自动配对设置Pin值
    public static boolean autoBond(Class btClass, BluetoothDevice device, String strPin) throws Exception {
        Method autoBondMethod = btClass.getMethod("setPin", new Class[]{byte[].class});
        Boolean result = (Boolean) autoBondMethod.invoke(device, new Object[]{strPin.getBytes()});
        return result;
    }

    static public boolean createBond(Class btClass, BluetoothDevice btDevice) throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    // 解除绑定
    static public boolean removeBond(Class btClass, BluetoothDevice btDevice) throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    static public boolean setPin(Class btClass, BluetoothDevice btDevice, String str) throws Exception {
        try {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[]{byte[].class});
            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice, new Object[]{str.getBytes()});
            Log.e("returnValue", "" + returnValue);

        } catch (SecurityException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();

        } catch (IllegalArgumentException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return true;
    }

    // 取消配对输入
    static public boolean cancelPairingUserInput(Class btClass, BluetoothDevice device) throws Exception {
        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
        // cancelBondProcess()
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    // 取消绑定过程
    static public boolean cancelBondProcess(Class btClass, BluetoothDevice device) throws Exception {
        Method createBondMethod = btClass.getMethod("cancelBondProcess");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    /**
     * Get InputDevice profile proxy.
     *
     * @return InputDevice flag of BluetoothProfile
     */
    public static int getInputDeviceHiddenConstant() {
        Class<BluetoothProfile> clazz = BluetoothProfile.class;
        for (Field f : clazz.getFields()) {
            int mod = f.getModifiers();
            if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && Modifier.isFinal(mod)) {
                try {
                    if (f.getName().equals("INPUT_DEVICE")) {
                        return f.getInt(null);
                    }
                } catch (Exception e) {
                    Logger.log(e.toString());
                }
            }
        }
        return -1;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean getInputDeviceIsExternal(Class clazz, InputDevice device) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method isExternalMethod = clazz.getMethod("isExternal");
        // cancelBondProcess()
        Boolean returnValue = (Boolean) isExternalMethod.invoke(device);
        return returnValue.booleanValue();
    }

    static public void printAllInform(Class clsShow) {
        try {
            // get all method include hide method
            Method[] hideMethod = clsShow.getMethods();
            int i = 0;
            for (; i < hideMethod.length; i++) {
                Log.e("method name", hideMethod[i].getName() + ";and the i is:" + i);

            }
            // 显示变量
            Field[] allFields = clsShow.getFields();
            for (i = 0; i < allFields.length; i++) {
                Log.e("Field name", allFields[i].getName());
            }

        } catch (SecurityException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();

        } catch (IllegalArgumentException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
