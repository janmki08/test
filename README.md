using UnityEngine;

public class PlayerController : MonoBehaviour
{
    public float moveSpeed = 5f; // 이동 속도
    public float jumpForce = 3f; // 점프 힘

    private bool isJumping = false;
    private bool isGrounded = false;

    // 마지막으로 보고 있는 방향을 기억하는 변수
    private bool isFacingRight = true;

    public Transform groundCheck;
    public float groundCheckRadius = 0.1f;
    public LayerMask whatIsGround;

    private Rigidbody2D rb;
    public AudioSource mySfx;
    public AudioClip jumpSound;
    public Animator playerAnimator;

    void Start()
    {
        rb = GetComponent<Rigidbody2D>();
    }

    void Update()
    {
        CheckGround();
        Move();
        Jump();
    }

    void CheckGround()
    {
        isGrounded = Physics2D.OverlapCircle(groundCheck.position, groundCheckRadius, whatIsGround);

        if (isGrounded)
        {
            isJumping = false;
            playerAnimator.SetBool("isJump", false);
        }
    }

    void Move()
    {
        float moveInput = Input.GetAxisRaw("Horizontal");
        Vector3 moveTo = new Vector3(moveInput * moveSpeed, rb.velocity.y);
        transform.position += moveTo * moveSpeed * Time.deltaTime;

        // 캐릭터가 이동 중일 때만 방향을 변경
        if (moveInput < 0) // 왼쪽으로 이동
        {
            GetComponent<SpriteRenderer>().flipX = true;
            isFacingRight = false;
            playerAnimator.SetBool("isMove", true);
        }
        else if (moveInput > 0) // 오른쪽으로 이동
        {
            GetComponent<SpriteRenderer>().flipX = false;
            isFacingRight = true;
            playerAnimator.SetBool("isMove", true);
        }
        else
        {
            // 이동이 멈추면 마지막 방향을 유지
            GetComponent<SpriteRenderer>().flipX = !isFacingRight;
            playerAnimator.SetBool("isMove", false);
        }
    }

    void Jump()
    {
        if (isGrounded && Input.GetKeyDown(KeyCode.Space))
        {
            isJumping = true;
            rb.AddForce(Vector3.up * jumpForce, ForceMode2D.Impulse);
            JumpSound();
            playerAnimator.SetBool("isJump", true);
            print("점프!" + isJumping);
        }
    }

    public void JumpSound()
    {
        mySfx.PlayOneShot(jumpSound);
    }

    private void OnCollisionEnter2D(Collision2D other)
    {
        if (other.gameObject.CompareTag("Step") || other.gameObject.CompareTag("Ground"))
        {
            isJumping = false;
            playerAnimator.SetBool("isJump", false);
            print("착지!" + isJumping);
        }
    }
}
