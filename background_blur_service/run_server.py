import io
import logging

from flask import Flask, request, send_file

from model import load_model, blur_background

application = Flask(__name__)

logger = logging.getLogger(__name__)
logging.basicConfig(format="%(asctime)s [%(levelname)s] %(message)s", level=logging.INFO, datefmt="%Y-%m-%d %H:%M:%S")


@application.route('/')
@application.route('/index')
def index():
    return "intended only for technical use"

@application.route("/process", methods=["POST"])
def handle_predict_request():
    image = request.files['img']
    print(request.files['img'])
    global model
    blurred_image = blur_background(model, image)
    
    # Create an in-memory byte stream buffer
    img_byte_io = io.BytesIO()

    # Convert the image to bytes and save it to the buffer
    blurred_image.save(img_byte_io, format='PNG')

    # Set the buffer position to the start of the stream
    img_byte_io.seek(0)

    # Return the image as a response with appropriate content type
    return send_file(img_byte_io, mimetype='image/png')


if __name__ == "__main__":
    try:
        model = load_model()
        application.run()
    except KeyboardInterrupt:
        logger.exception("Shutting down")
    except Exception:
        logger.exception("Error in initialization chain")