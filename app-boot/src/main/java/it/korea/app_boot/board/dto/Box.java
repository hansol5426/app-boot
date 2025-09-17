package it.korea.app_boot.board.dto;


public class Box {

    private final String name;
    private final int count;

    private Box(BoxBuilder builder){
        this.name = builder.name;
        this.count = builder.count;
    }

    public static BoxBuilder builder(){
        return new BoxBuilder();
    }

    public String getName(){
        return this.name;
    }

    public int getCount(){
        return this.count;
    }

    public static class BoxBuilder{
        private String name;
        private int count;

        private BoxBuilder(){}     
      
        public BoxBuilder name(String name){
            this.name = name;
            return this;
        }

         public BoxBuilder count(int count){
            this.count = count;
            return this;
        }

        public Box build(){
            return new Box(this);
        }
    }

    // 예시 >> 실제로는 있으면 안됨
    // public static void main(String[] args) {
    //     Box b = Box.builder() 
    //                 .name("사과박스") 
    //                 .count(10)
    //                 .build();

    // }
}
