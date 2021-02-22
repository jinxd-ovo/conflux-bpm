mode: 'horizontal',                 // 'horizontal', 'vertical', 'fade' 定义slider滚动的方向，有三个值可供选择 
infiniteLoop: true,                 // true, false - display first slide after last 无限循环 
hideControlOnEnd: false,            // true, false - if true, will hide 'next' control on last slide and 'prev' control on first 如果设置true，将会在最后一个幻灯片隐藏“next”，在最前面的幻灯片因此“prev” 
controls: true,                     // true, false - previous and next controls 是否显示“previous”和“next”按钮 
speed: 500,                         // integer - in ms, duration of time slide transitions will occupy   速度，单位为毫秒 
easing: 'swing',                    // used with jquery.easing.1.3.js - see http://gsgd.co.uk/sandbox/jquery/easing/ for available options 
pager: true,                        // true / false - display a pager 
pagerSelector: null,                // jQuery selector - element to contain the pager. ex: '#pager' 
pagerType: 'full',                  // 'full', 'short' - if 'full' pager displays 1,2,3... if 'short' pager displays 1 / 4   如果设置full，将显示1，2，3……，如果设置short，将显示1/4 . 
pagerLocation: 'bottom',            // 'bottom', 'top' - location of pager 页码的位置 
pagerShortSeparator: '/',           // string - ex: 'of' pager would display 1 of 4 页面分隔符 
pagerActiveClass: 'pager-active',   // string - classname attached to the active pager link 当前页码的className 
nextText: 'next',                   // string - text displayed for 'next' control 下一页的文字 
nextImage: '',                      // string - filepath of image used for 'next' control. ex: 'images/next.jpg' 可以设置下一页为图片 
nextSelector: null,                 // jQuery selector - element to contain the next control. ex: '#next' 
prevText: 'prev',                   // string - text displayed for 'previous' control 上一页的文字 
prevImage: '',                      // string - filepath of image used for 'previous' control. ex: 'images/prev.jpg' 上一页的图片 
prevSelector: null,                 // jQuery selector - element to contain the previous control. ex: '#next' 
captions: false,                    // true, false - display image captions (reads the image 'title' tag) 是否显示图片的标题，读取图片的title属性的内容。 
  
captionsSelector: null,             // jQuery selector - element to contain the captions. ex: '#captions' 
auto: false,                        // true, false - make slideshow change automatically 幻灯片自动滚动 
autoDirection: 'next',              // 'next', 'prev' - direction in which auto show will traverse 自动滚动的顺序 
autoControls: false,                // true, false - show 'start' and 'stop' controls for auto show 自动滚动的控制键 
autoControlsSelector: null,         // jQuery selector - element to contain the auto controls. ex: '#auto-controls' 
autoStart: true,                    // true, false - if false show will wait for 'start' control to activate  
autoHover: false,                   // true, false - if true show will pause on mouseover 设置鼠标mouseover将会使自动滚动暂停 
autoDelay: 0,                       // integer - in ms, the amount of time before starting the auto show 
pause: 3000,                        // integer - in ms, the duration between each slide transition  过渡时间 
startText: 'start',                 // string - text displayed for 'start' control 开始按钮的文字 
startImage: '',                     // string - filepath of image used for 'start' control. ex: 'images/start.jpg' 开始按钮的图片 
stopText: 'stop',                   // string - text displayed for 'stop' control 停止按钮的文本 
stopImage: '',                      // string - filepath of image used for 'stop' control. ex: 'images/stop.jpg'   停止按钮的图片 
ticker: false,                      // true, false - continuous motion ticker mode (think news ticker) 
                                    // note: autoControls and autoControlsSelector apply to ticker! 
tickerSpeed: 5000,                  // integer - has an inverse effect on speed. therefore, a value of 10000 will  
                                    // scroll very slowly while a value of 50 will scroll very quickly. 
tickerDirection: 'next',            // 'next', 'prev' - direction in which ticker show will traverse 
tickerHover: false,                 // true, false - if true ticker will pause on mouseover 
wrapperClass: 'bx-wrapper',         // string - classname attached to the slider wraper 
startingSlide: 0,                   // integer - show will start on specified slide. note: slides are zero based! 
displaySlideQty: 1,                 // integer - number of slides to display at once 
moveSlideQty: 1,                    // integer - number of slides to move at once 
randomStart: false,                 // true, false - if true show will start on a random slide